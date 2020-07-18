package com.example.recyclesample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.GsonBuilder
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoreActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    val channelId = "com.example.siakscraper"
    private val description = "New Score Notification (Local)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN

            notificationManager.createNotificationChannel(notificationChannel)
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("Scores are different from last scraping session's results")
                .setContentText("Possibility of scores update")
                .setSmallIcon(R.drawable.bird_logo)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.bird_logo))
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle("Scores are different from last scraping session's results")
                .setContentText("Possibility of scores update")
                .setSmallIcon(R.drawable.bird_logo)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.bird_logo))
        }


        val scoresJSON = getIntent().getStringExtra("JSON_data")
        val scoresPreference = ScorePreference(this)

        if (!scoresJSON.toString().equals(scoresPreference.getScores())) {
            notificationManager.notify(1, builder.build())
            scoresPreference.setScores(scoresJSON.toString())
        }

        val gson = GsonBuilder().create()
        val serverResponse = gson.fromJson(scoresJSON, ServerResponse::class.java)

        val listView : ListView = findViewById(R.id.list_view_score)
        listView.adapter = ListAdapter(this, R.layout.parent_item, serverResponse.details, serverResponse.scores)

        // Update the last updated text
        val last_update_text : TextView = findViewById(R.id.last_update_text)
        last_update_text.text = getIntent().getStringExtra("date_updated")

    }

    inner class ListAdapter(var context : Context, var itemView : Int, var itemList : MutableList<Detail>, var scoresList : MutableList<Score>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val inflater : LayoutInflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.parent_item, viewGroup, false) as LinearLayout

            view.findViewById<TextView>(R.id.title_text).text = itemList.get(position).name
            view.findViewById<TextView>(R.id.score_text).text = scoresList.get(position).grade_number + "/" + scoresList.get(position).grade_letter

            for (component in itemList.get(position).scores) {
                val new_subitem = layoutInflater.inflate(R.layout.sample_item, null)
                new_subitem.findViewById<TextView>(R.id.main_text).text = component.component
                new_subitem.findViewById<TextView>(R.id.secondary_text).text = component.score

                view.addView(new_subitem)
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return this.itemList.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return itemList.size
        }

    }
}