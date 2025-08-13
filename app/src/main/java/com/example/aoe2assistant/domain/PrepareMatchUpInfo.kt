package com.example.aoe2assistant.domain

import com.example.aoe2assistant.data.CivInfo
import com.example.aoe2assistant.data.CivInfoMatchUp
import com.example.aoe2assistant.data.MapInfo
import com.example.aoe2assistant.data.MapInfoMatchUp

fun PrepareMatchUpInfo(myCivInfo: CivInfo,
                        oppCivInfo: CivInfo,
                        mapInfo: MapInfo): Triple<CivInfoMatchUp, CivInfoMatchUp, MapInfoMatchUp> {

    val myciv = CivInfoMatchUp(myCivInfo.name, myCivInfo.type)
    for (cat in myCivInfo.getCategories()){
        val temp = myCivInfo.getBonus(cat)
        myciv.setBonus(cat,

                if(temp.isNullOrEmpty()){
                    listOf("")
                }
                else{
                    temp.toList()
                }

            )

    }

    val oppciv = CivInfoMatchUp(oppCivInfo.name, oppCivInfo.type)
    for (cat in oppCivInfo.getCategories()){
        val temp = oppCivInfo.getBonus(cat)
        oppciv.setBonus(cat,

            if(temp.isNullOrEmpty()){
                listOf("")
            }
            else{
                temp.toList()
            }

        )

    }

    val mapInfoMU = MapInfoMatchUp(mapInfo.name, mapInfo.type)
    for (cat in mapInfo.getCategories()){
        val temp = mapInfo.getBonus(cat)
        mapInfoMU.setBonus(cat,

            if(temp.isNullOrEmpty()){
                listOf("")
            }
            else{
                temp.toList()
            }

        )

    }

    return Triple<CivInfoMatchUp, CivInfoMatchUp, MapInfoMatchUp>(myciv, oppciv, mapInfoMU)

}