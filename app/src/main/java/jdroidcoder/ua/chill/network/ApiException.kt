package jdroidcoder.ua.chill.network

import java.io.IOException

/**
 * Created by jdroidcoder on 16.02.2018.
 */
class ApiException(var status: String?, var msg: String?) : IOException()
