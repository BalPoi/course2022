package by.gsu.bal.curse.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.gsu.bal.curse.Constants;
import by.gsu.bal.curse.Post;
import by.gsu.bal.curse.R;

public class PostActivity extends AppCompatActivity {
    TextView tvTitle, tvCost, tvCity, tvDate, tvDescription, tvContacts;
    ImageCarousel carousel;
    Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        init();

        List<CarouselItem> images = new ArrayList<>();
        for (String url : post.getImagesURLs()) {
                images.add(new CarouselItem(url)
            );
        }

        carousel.setData(images);

        double cost = post.getCost();
        String costStr;
        if (cost == (long) cost) {
            costStr = String.format("%d", (long) cost);
        } else {
            costStr = String.format("%.2f", cost);
        }
        costStr += " р.";
        String title = post.getTitle();
        String city = post.getCity();
        String description = post.getDescription();
        String contacts = post.getContacts();

        LocalDateTime date = LocalDateTime.parse(post.getPublicationDate(), Constants.ISO8601);
        String dateStr = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(date);

        tvCost.setText(costStr);
        tvTitle.setText(title);
        tvCity.setText(city);
        tvDate.setText(dateStr);
        tvDescription.setText(description);
        tvContacts.setText(contacts);
    }

    private void init() {
        tvTitle = findViewById(R.id.tvTitle);
        tvCost = findViewById(R.id.tvCost);
        tvCity = findViewById(R.id.tvCity);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvContacts = findViewById(R.id.tvContacts);
        carousel = findViewById(R.id.carousel);

        carousel.registerLifecycle(getLifecycle());

        post = getIntent().getParcelableExtra("post");
    }
}
