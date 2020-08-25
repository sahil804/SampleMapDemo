package com.example.samplemapdemo.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.samplemapdemo.R
import com.example.samplemapdemo.data.model.LocationInfo
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_add_marker_dialog.*

class AddMarkerDialogFragment : DaggerAppCompatDialogFragment() {

    var onSaveListener: (LocationInfo) -> Unit = {}
    var onDismissListener: () -> Unit = {}

    lateinit var locationInfo: LocationInfo

    companion object {
        private const val PARAM_LOCATION = "location_key"
        fun newInstance(locationInfo: LocationInfo): AddMarkerDialogFragment {
            val fragment = AddMarkerDialogFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(PARAM_LOCATION, locationInfo)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_marker_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            locationInfo = it.getParcelable<LocationInfo>(PARAM_LOCATION) as LocationInfo
        }
        addMarkerTitle.text =
            getString(R.string.latitude_longitude_selected_are, locationInfo.lat, locationInfo.lng)
        btCancel.setOnClickListener {
            onDismissListener.invoke()
            dismiss()
        }
        btSave.setOnClickListener {
            locationDescription.text.toString().takeIf { it.isNotEmpty() }?.let {
                locationInfo.description = it
            }
            onSaveListener(locationInfo)
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDismissListener.invoke()
    }
}