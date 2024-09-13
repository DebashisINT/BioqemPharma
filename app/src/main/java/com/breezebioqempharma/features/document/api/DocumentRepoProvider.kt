package com.breezebioqempharma.features.document.api

import com.breezebioqempharma.features.dymanicSection.api.DynamicApi
import com.breezebioqempharma.features.dymanicSection.api.DynamicRepo

object DocumentRepoProvider {
    fun documentRepoProvider(): DocumentRepo {
        return DocumentRepo(DocumentApi.create())
    }

    fun documentRepoProviderMultipart(): DocumentRepo {
        return DocumentRepo(DocumentApi.createImage())
    }
}