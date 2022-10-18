package com.project.navermap.presentation.MainActivity.myinfo.like

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.project.navermap.databinding.FragmentLikeListBinding
import com.project.navermap.domain.model.LikeItemModel
import com.project.navermap.domain.model.LikeMarketModel
import com.project.navermap.domain.model.Model
import com.project.navermap.domain.model.category.LikeCategory
import com.project.navermap.presentation.MainActivity.MainViewModel
import com.project.navermap.presentation.MainActivity.store.storeDetail.StoreDetailViewModel
import com.project.navermap.presentation.base.BaseFragment
import com.project.navermap.util.provider.ResourcesProvider
import com.project.navermap.widget.adapter.ModelRecyclerAdapter
import com.project.navermap.widget.adapter.listener.LikeListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LikeListFragment<T : Model> : BaseFragment<FragmentLikeListBinding>() {

    override fun getViewBinding() =
        FragmentLikeListBinding.inflate(layoutInflater)

    private val category by lazy {
        requireArguments().getSerializable(FRAGMENT_CATEGORY) as LikeCategory
    }


    private val activityViewModel by activityViewModels<LikeListViewModel<T>>()

    @Inject
    lateinit var resourcesProvider: ResourcesProvider

    private val adapter by lazy {
        ModelRecyclerAdapter<T, LikeListViewModel<T>>(
            listOf<T>(),activityViewModel, resourcesProvider,
            object : LikeListener {
                override fun onClick(model: Model) {
                    if (category == LikeCategory.MARKET) {
                        Toast.makeText(
                            context,
                            "LikeMarketModel : ${(model as LikeMarketModel).marketName}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            context,
                            "LikeItemModel : ${(model as LikeItemModel).itemName}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                @Suppress("UNCHECKED_CAST")
                override fun onDeleteClick(model: Model) {
                    activityViewModel.delete(model as T)
                }
            }
        )
    }

    override fun initState(){
        super.initState()
    }

    override fun initViews() {
        super.initViews()

        binding.likeListRecyclerView.adapter = adapter
    }

    override fun observeData() {
        activityViewModel.likeData.observe(viewLifecycleOwner) {
            when (it) {
                is LikeState.Uninitialized -> activityViewModel.fetchData()

                is LikeState.Loading -> {
                    // TODO: 2022.02.21 Handle loading
                }

                is LikeState.Success<*> -> adapter.submitList(it.modelList)

                is LikeState.Error -> {
                    // TODO: 2022.02.21 Handle error
                }
            }
        }
    }

    companion object {
        const val FRAGMENT_CATEGORY = "FRAGMENT_CATEGORY"

        fun newInstance(category: LikeCategory): LikeListFragment<out Model> {
            val likeListFragment = if (category == LikeCategory.MARKET) {
                LikeListFragment<LikeMarketModel>()
            } else {
                LikeListFragment<LikeItemModel>()
            }

            return likeListFragment.apply {
                arguments = bundleOf(FRAGMENT_CATEGORY to category)
            }
        }
    }
}