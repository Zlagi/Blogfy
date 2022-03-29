package com.zlagi.network.fakes

import com.zlagi.data.connectivity.ConnectivityChecker
import javax.inject.Inject

class FakeConnectivityCheckReturnSuccess @Inject constructor() : ConnectivityChecker {
    override fun hasInternetAccess(): Boolean = true
}

class FakeConnectivityCheckReturnError : ConnectivityChecker {
    override fun hasInternetAccess(): Boolean = false
}
