package jmapps.hadith40.presentation.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import jmapps.hadith40.R
import jmapps.hadith40.data.database.DatabaseLists
import jmapps.hadith40.presentation.mvp.main.MainContract
import jmapps.hadith40.presentation.mvp.main.MainPresenter
import jmapps.hadith40.presentation.ui.about.AboutUsBottomSheet
import jmapps.hadith40.presentation.ui.chapters.AdapterChapter
import jmapps.hadith40.presentation.ui.chapters.ModelChapter
import jmapps.hadith40.presentation.ui.settings.SettingsBottomSheet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    TextWatcher, MainContract.View, AdapterChapter.OnItemClick {

    private var keyNightMode = "key_night_mode"

    private var mainPresenter: MainPresenter? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var swNightMode: Switch

    private lateinit var chapterList: MutableList<ModelChapter>
    private lateinit var adapterChapter: AdapterChapter

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = preferences.edit()

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mainPresenter = MainPresenter(this, this)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        etSearchByChapters.addTextChangedListener(this)

        navigationView.menu.findItem(R.id.nav_night_mode).actionView = Switch(this)
        swNightMode = navigationView.menu.findItem(R.id.nav_night_mode).actionView as Switch
        swNightMode.isClickable = false
        swNightMode.isChecked = preferences.getBoolean(keyNightMode, false)

        initView()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_settings -> mainPresenter?.getSettings()

            R.id.nav_night_mode -> {
                mainPresenter?.getNightMode(!swNightMode.isChecked)
                swNightMode.isChecked = !swNightMode.isChecked
            }

            R.id.nav_about_us -> mainPresenter?.getAboutUs()

            R.id.nav_rate -> mainPresenter?.getRate()

            R.id.nav_share -> mainPresenter?.getShare()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun initView() {
        mainPresenter?.getNightMode(swNightMode.isChecked)

        chapterList = DatabaseLists(this).getChapterList

        val verticalList = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMainChapters.layoutManager = verticalList

        adapterChapter = AdapterChapter(chapterList, this)
        rvMainChapters.adapter = adapterChapter
    }

    override fun itemClick(chapterId: Int) {
        Toast.makeText(this, "Click = $chapterId", Toast.LENGTH_LONG).show()
    }

    override fun setSettings() {
        val settingsBottomSheet = SettingsBottomSheet()
        settingsBottomSheet.setStyle(STYLE_NORMAL, R.style.BottomSheetStyleFull)
        settingsBottomSheet.show(supportFragmentManager, "settings")
    }

    override fun saveNightModeState(state: Boolean) {
        editor.putBoolean(keyNightMode, state).apply()
    }

    override fun setAboutUs() {
        val aboutUsBottomSheet = AboutUsBottomSheet()
        aboutUsBottomSheet.setStyle(STYLE_NORMAL, R.style.BottomSheetStyleFull)
        aboutUsBottomSheet.show(supportFragmentManager, "about_us")
    }
}