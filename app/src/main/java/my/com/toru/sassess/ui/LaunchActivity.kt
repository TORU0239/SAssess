package my.com.toru.sassess.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_launch.*
import my.com.toru.sassess.R
import my.com.toru.sassess.SassApp
import my.com.toru.sassess.ui.presenter.LaunchPresenterImp
import my.com.toru.sassess.ui.view.LaunchPresenter
import my.com.toru.sassess.ui.view.LaunchView
import my.com.toru.sassess.ui.view.MainView
import my.com.toru.sassess.util.Util
import my.com.toru.sassess.util.actionAndRequestPermission


class LaunchActivity : AppCompatActivity(), LaunchView {

    companion object {
        val TAG = LaunchActivity::class.java.simpleName!!
    }

    private lateinit var ctx: Activity
    private lateinit var presenter:LaunchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        ctx = this@LaunchActivity
        presenter = LaunchPresenterImp(this@LaunchActivity)

        initializeSavedLocation()
        presenter.checkPermission()
    }

    private fun initializeSavedLocation(){
        with(application as SassApp){
            fixedCurrentLatitude = 0.0
            fixedCurrentLongitude = 0.0
        }
    }
    override fun getViewContext(): Context = this@LaunchActivity

    override fun showPermissionSnackbar() {
        Util.makePermissionSnackbar(container).actionAndRequestPermission(ctx)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(!presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}