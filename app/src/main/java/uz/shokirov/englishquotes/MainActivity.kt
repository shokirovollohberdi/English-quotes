package uz.shokirov.englishquotes

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import me.ibrahimsn.lib.OnItemSelectedListener
import uz.shokirov.adaoter.PagerAdapter
import uz.shokirov.englishquotes.databinding.AboutDialogBinding
import uz.shokirov.englishquotes.databinding.ActivityMainBinding
import uz.shokirov.englishquotes.databinding.FeedbackDialogBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: PagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setAdapter()
        setNavigation()


    }

    private fun setNavigation() {
        binding.imageMenu.setOnClickListener {
            binding.drawable.openDrawer(Gravity.LEFT)
        }
        binding.navigation.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.menu_about -> {
                        aboutDialog()
                    }
                    R.id.menu_feedback -> {
                        feedbackDialog()
                    }
                    R.id.menu_share -> {

                    }
                    R.id.menu_author->{
                        startActivity(Intent(this@MainActivity,AuthorActivity::class.java))
                    }

                }
                return true
            }
        })

        binding.tabLayout.setOnItemSelectedListener {
            binding.viewpager.currentItem = it
        }



        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.d("TAG", "onPageScrolled: $position")
                binding.tabLayout.itemActiveIndex = position

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })


    }

    private fun feedbackDialog() {
        var dialog = AlertDialog.Builder(this).create()
        var item = FeedbackDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        
        item.cardTelegram.setOnClickListener { 
            intentLink("https://t.me/shokirov_ollohberdi")
        }
        item.cardInstagram.setOnClickListener {
            intentLink("https://www.instagram.com/ollohberdi_shokirov/")
        }
        item.cardPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+99891661180")
            startActivity(dialIntent)
        }

    }

    private fun intentLink(url: String) {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(myIntent)
    }

    private fun aboutDialog() {
        var dialog = AlertDialog.Builder(this).create()
        var item = AboutDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    private fun setAdapter() {
        adapter = PagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = adapter
    }
}