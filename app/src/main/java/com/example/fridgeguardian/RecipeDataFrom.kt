package com.example.fridgeguardian

import java.io.Serializable

data class RecipeDataForm(
    val RCP_SEQ: Int?,
    val RCP_NM: String?,
    val RCP_WAY2: String?,
    val INFO_WGT: String?,
    val INFO_ENG: Float?,
    val INFO_CAR: Float?,
    val INFO_PRO: Float?,
    val INFO_FAT: Float?,
    val INFO_NA: Float?,
    val HASH_TAG: String?,
    val ATT_FILE_NO_MK: String?,
    val RCP_PARTS_DTLS: String?,
    val MANUAL01: String?,
    val MANUAL02: String?,
    val MANUAL03: String?,
    val MANUAL04: String?,
    val MANUAL05: String?,
    val MANUAL06: String?,
    val MANUAL07: String?,
    val MANUAL08: String?,
    val MANUAL09: String?,
    val MANUAL10: String?,
    val MANUAL11: String?,
    val MANUAL12: String?,
    val MANUAL13: String?,
    val MANUAL14: String?,
    val MANUAL15: String?,
    val MANUAL16: String?,
    val MANUAL17: String?,
    val MANUAL18: String?,
    val MANUAL19: String?,
    val MANUAL20: String?,
    val RCP_NA_TIP: String?
): Serializable

