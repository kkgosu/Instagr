package com.example.instagr.data.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations


fun <A,B> LiveData<A>.map (f: (A) -> B): LiveData<B> =
    Transformations.map(this, f)