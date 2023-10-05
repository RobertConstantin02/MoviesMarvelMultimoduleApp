package com.example.domain_model.common

class ImageUrlBo(val url: String?) {

    val value: String?

    init {
        if (url.isNullOrEmpty()) this.value = null
        else this.value = url
    }
}
