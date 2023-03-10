package com.project.navermap.presentation.mainActivity.store

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.project.navermap.R
import com.project.navermap.data.entity.firebase.ReviewEntity
import com.project.navermap.databinding.FragmentStoreMarketReviewBinding
import com.project.navermap.domain.model.FirebaseModel
import com.project.navermap.domain.model.ReviewModel
import com.project.navermap.presentation.base.BaseFragment
import com.project.navermap.presentation.mainActivity.store.storeDetail.review.StoreReviewFragment
import com.project.navermap.util.provider.ResourcesProvider
import com.project.navermap.widget.adapter.ModelRecyclerAdapter
import com.project.navermap.widget.adapter.listener.FirebaseListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FirebaseReviewFragment : BaseFragment<FragmentStoreMarketReviewBinding>() {

    override fun getViewBinding() = FragmentStoreMarketReviewBinding.inflate(layoutInflater)

    private lateinit var alertDialog: Dialog
    private lateinit var et_title: EditText
    private lateinit var et_content: EditText
    private lateinit var ratingBar: RatingBar

    val viewModel by viewModels<StoreReviewViewModel>()

    @Inject
    lateinit var resourcesProvider: ResourcesProvider

    private val marketId by lazy {
        requireArguments().getString(KEY_MARKET_ID)!!
    }

    private val adapter by lazy {
        ModelRecyclerAdapter<ReviewModel, StoreReviewViewModel>(
            listOf(),viewModel,resourcesProvider,
            adapterListener = object : FirebaseListener {
                override fun onClickItem(model: FirebaseModel) {

                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()
        binding.ReviewRecyclerView.adapter = adapter
        viewModel.getReviewByMarketId(marketId)
//        viewModel.write("0","??????","??????",4)

        val dialogView = layoutInflater.inflate(R.layout.dialog_review_write, null)
        et_title = dialogView.findViewById(R.id.et_title)
        et_content = dialogView.findViewById(R.id.et_content)

        // ratingBar ??????
        ratingBar = dialogView.findViewById(R.id.reviewRatingBar)
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // ????????? ?????? 1???
            if (rating < 1)
                ratingBar.rating = 1.0f
        }

        // applicationContext ?????? baseContext ?????? x
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog.setCancelable(false) // ??????????????? ?????? ?????? ???????????? dismiss?????? ?????? ??????

        binding.button.setOnClickListener {
            alertDialog.show()
        }

        /*
            ?????? ?????? ?????? ??????
        */
        val btn_cancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { alertDialog.dismiss() }


        /*
            ???????????? ?????? ?????? ??????
         */
        val btn_write = dialogView.findViewById<Button>(R.id.btn_write)
        btn_write.setOnClickListener {

            if (et_title.text.isBlank() || et_content.text.isBlank()) {
                Toast.makeText(requireContext(), "?????? ?????? ????????????!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ????????? ????????? ????????? ??????
            if (ratingBar.rating < 1) {
                Toast.makeText(requireContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.write(marketId,
                et_title.text.toString(),
                et_content.text.toString(),
                ratingBar.rating.toInt()
            )

            alertDialog.dismiss()
        }
    }

    override fun observeData() {
        viewModel.reviewData.observe(viewLifecycleOwner){
           Log.d("TAG","observer $it")
            adapter.submitList(it)
        }
    }

        companion object {
            private const val KEY_MARKET_ID = "MARKET_ID"

            fun newInstance(marketId : String) : FirebaseReviewFragment {
                return FirebaseReviewFragment().apply {
                    arguments = bundleOf(KEY_MARKET_ID to marketId)
                }
            }
        }
}