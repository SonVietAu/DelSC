package com.delsc.viewmodel

import com.google.firebase.database.FirebaseDatabase

val database =
    FirebaseDatabase.getInstance("https://sugar-cane-delivery-default-rtdb.asia-southeast1.firebasedatabase.app/")

val myRef = database.getReference()

