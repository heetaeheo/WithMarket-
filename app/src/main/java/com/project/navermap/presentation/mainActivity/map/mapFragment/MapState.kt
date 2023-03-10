package com.project.navermap.presentation.mainActivity.map.mapFragment

import androidx.annotation.StringRes
import com.project.navermap.data.entity.LocationEntity
import com.project.navermap.domain.model.RestaurantModel


sealed class MapState {
    object Uninitialized : MapState()
    object Loading : MapState()

    data class Success(
        val restaurantInfoList: MutableList<RestaurantModel>,
        val destLocation: LocationEntity? = null
    ) : MapState()

    data class Error(
        @StringRes val id: Int
    ) : MapState()
}
