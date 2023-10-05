package com.example.domain_model.location

import android.net.Uri

data class LocationBo(
    val url: String?,
    val name: String?
) {
    val locationId =  Uri.parse(url).lastPathSegment?.toInt()


}
