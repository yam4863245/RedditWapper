package com.jg.redditexample.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import com.jg.redditexample.R
import com.jg.redditexample.model.Post
import com.jg.redditexample.utils.loadImage
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
/*            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(
                        ItemDetailFragment.ARG_ITEM_ID,
                            intent.getSerializableExtra(ItemDetailFragment.ARG_ITEM_ID))
                }
            }*/

            val item = intent.getSerializableExtra(ItemDetailFragment.ARG_ITEM_ID) as Post?
            val img = findViewById<ImageView>(R.id.imageViewThumb)
            img.loadImage(item?.url)
            img.setOnClickListener{
                Log.d("thumbnail",item?.thumbnail)
                Log.d("dest",item?.url)
            }

/*            supportFragmentManager.beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit()*/
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    navigateUpTo(Intent(this, ItemListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
