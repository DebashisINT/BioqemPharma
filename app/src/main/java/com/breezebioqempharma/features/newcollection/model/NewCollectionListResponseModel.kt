package com.breezebioqempharma.features.newcollection.model

import com.breezebioqempharma.app.domain.CollectionDetailsEntity
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.shopdetail.presentation.model.collectionlist.CollectionListDataModel

/**
 * Created by Saikat on 15-02-2019.
 */
class NewCollectionListResponseModel : BaseResponse() {
    //var collection_list: ArrayList<CollectionListDataModel>? = null
    var collection_list: ArrayList<CollectionDetailsEntity>? = null
}