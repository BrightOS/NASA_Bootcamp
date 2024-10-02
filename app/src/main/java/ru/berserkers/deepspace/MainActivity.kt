package ru.berserkers.deepspace

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import ru.berserkers.deepspace.data.fb_general.MFirebaseUser
import ru.berserkers.deepspace.databinding.ActivityMainBinding
import ru.berserkers.deepspace.databinding.NavHeaderMainBinding
import ru.berserkers.deepspace.utils.Data
import ru.berserkers.deepspace.utils.DimensionsUtil
import java.io.IOException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var interactionResult: ActivityResultLauncher<Intent>

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.navView.setupWithNavController(navController)

        navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        changeHeader()

        interactionResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    setImage(it.data)
                }
            }

        // Azure notifications
    //    NotificationHub.setListener(AzureNotificationListener(this))
        val connectionString = this.resources.getString(R.string.azure_connection_string)
        val hubName = this.resources.getString(R.string.azure_hub_name)
  //      NotificationHub.start(this.application, hubName, connectionString)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        binding.mainLoading.stopLoadingAnimation(false)
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun setImage(data: Intent?) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
            navHeaderMainBinding.userAvatar.setImageBitmap(bitmap)
            val user = FirebaseAuth.getInstance()
            if (data?.data != null) {
                FirebaseStorage.getInstance().getReference("user_data/${user.uid}")
                    .putFile(data.data!!)
            }
        } catch (e: IOException) {
            Toast.makeText(
                this,
                "Please, try another photo! (${e.message})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // open drawer from fragment
    fun openDrawer() {
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun changeHeader() {
        val mFirebaseUser = MFirebaseUser()
        if (mFirebaseUser.isUserAuthenticated()) {
            lifecycleScope.launchWhenStarted {
                navHeaderMainBinding.userAvatar.setOnClickListener {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra("requestCode", PICK_IMAGE_REQUEST)
                    interactionResult.launch(Intent.createChooser(intent, "Select avatar"))
                }
                when (val it = mFirebaseUser.getUserAvatar()) {
                    is Data.Ok -> {
                        navHeaderMainBinding.userAvatar.foreground = null
                        navHeaderMainBinding.userAvatar.setImageBitmap(it.data)
                    }
                    is Data.Error -> {
                        navHeaderMainBinding.userAvatar.setImageBitmap(null)
                        navHeaderMainBinding.userAvatar.foreground =
                            getDrawable(R.drawable.ic_photo_mini)
                    }

                    is Data.Loading -> TODO("Show progress bar")
                    is Data.Local -> TODO()
                }
            }
            navHeaderMainBinding.signIn.text = getString(R.string.sign_out)
            navHeaderMainBinding.signIn.setOnClickListener {
                mFirebaseUser.signOutUser(this)
                changeHeader()
            }
        } else {
            //binding.navView.menu.findItem(R.id.createPostFragment).isVisible = false
            navHeaderMainBinding.userAvatar.setImageBitmap(null)
            navHeaderMainBinding.userAvatar.foreground = getDrawable(R.drawable.ic_login_mini)

            navHeaderMainBinding.userAvatar.setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.nav_host_fragment).navigate(R.id.login)
            }
            navHeaderMainBinding.signIn.text = getString(R.string.log_in)
            navHeaderMainBinding.signIn.setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.nav_host_fragment).navigate(R.id.login)
            }
        }

        DimensionsUtil.setMargins(
            navHeaderMainBinding.userAvatar,
            0,
            DimensionsUtil.getStatusBarHeight(resources),
            0,
            0
        )
    }

    fun stopLoadingAnimation(showCheckIcon: Boolean? = null) {
        binding.mainLoading.apply {
            if (showCheckIcon == null)
                stopLoadingAnimation()
            else stopLoadingAnimation(showCheckIcon)
        }
    }

    fun startLoadingAnimation() {
        binding.mainLoading.startLoadingAnimation()
    }

    fun showError(errorText: String) {
        binding.mainLoading.showError(errorText)
    }

    fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}
