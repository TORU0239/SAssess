package my.com.toru.sassess.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class AdvCallback<T>(private val failedCallback:()->Unit,
                          private val successCallback:(Response<T>)->Unit): Callback<T> {

    override fun onFailure(call: Call<T>, t: Throwable) {
        failedCallback()
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        when(response.code()){
            200->{
                successCallback(response)
            }
            else->{
                failedCallback()
            }
        }
    }
}