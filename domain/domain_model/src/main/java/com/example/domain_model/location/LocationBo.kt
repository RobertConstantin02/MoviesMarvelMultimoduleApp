package com.example.domain_model.location

import android.net.Uri
import android.util.Log
import java.net.URI

data class LocationBo(
    val url: String?,
    val name: String?
) {
    val locationId: Int? =  getId() //Uri.parse(url).lastPathSegment?.toInt()
    private fun getId() =
        if (!url.isNullOrEmpty()) url.let { URI(it).path.split("/").last().toInt() }
        else null

}
