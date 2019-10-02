package `in`.evilcorp.bricksopenlauncher.presentation

import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

//Used to allow enbling/disabling intent-filter 'HOME' in runtime.
class HomeStubActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        Timber.d("HomeStubActivity onRESUME")
        MainActivity.launch(this)
    }
}
