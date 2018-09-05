package my.com.toru.sassess.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class AdvCallback<T>(private val failedCallback:()->Unit,
                          private val successCallback:(Response<T>)->Unit): Callback<T> {

    private val THRESHOLD = 2
    private var count = 0

    override fun onFailure(call: Call<T>, t: Throwable) {
        retryCall(call)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if(response.isSuccessful){
            when(response.code()){
                200->{
                    successCallback(response)
                }
                else->{
                    failedCallback()
                }
            }
        }
        else{
            retryCall(call)
        }
    }

    private fun retryCall(call:Call<T>){
        if(count < THRESHOLD){
            call.clone().enqueue(this@AdvCallback)
            count+=1
        }
        else{
            failedCallback()
        }
    }
}