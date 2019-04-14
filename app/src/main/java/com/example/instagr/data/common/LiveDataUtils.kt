package com.example.instagr.data.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations


fun <A,B> LiveData<A>.map (f: (A) -> B): LiveData<B> =
    Transformations.map(this, f)