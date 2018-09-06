package my.com.toru.sassess.ui.presenter

interface BasePresenter

interface LaunchPresenter:BasePresenter{
    fun checkPermission()
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
}
interface MainPresenter:BasePresenter{
    fun requestPermission()
    fun checkPermissionGranted():Boolean
    fun requestPickupPoint(start:Long, end:Long)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
}