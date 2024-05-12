package com.nghiatd.bt4.controller

import com.nghiatd.bt4.model.CountExt

class Manager private constructor(){
    companion object {
        private var _instance: Manager? = null
        fun getIns() : Manager {
            if (_instance == null) {
                _instance = Manager()
            }
            return requireNotNull(_instance)
        }
    }
    private lateinit var countExt : CountExt

    fun initLoad(){

    }

    fun addCount(){

    }

    fun resetCount(){

    }
}