package by.gsu.bal.curse.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MyMarkerRenderer extends DefaultClusterRenderer<StationMarker> {

    private final Context mContext;
    private final IconGenerator iconGenerator;

    public MyMarkerRenderer(Context context, GoogleMap map, ClusterManager<StationMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        iconGenerator = new IconGenerator(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull StationMarker item, @NonNull MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(item.getCountStatus())));
    }

    public Bitmap textAsBitmap(String text) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.WHITE);
        bgPaint.setAntiAlias(true);

        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        final int height = 100;
        final int width = 100;

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawCircle(width / 2F, height / 2F, height / 2F, bgPaint);
        bgPaint.setColor(Color.YELLOW);
        canvas.drawCircle(width / 2F, height / 2F, height / 2F - 5, bgPaint);
        canvas.drawText(text, width / 2F, height / 2F + bounds.height() / 2F, textPaint);
        return image;
    }

    @Override
    protected void onBeforeClusterRendered(
            @NonNull Cluster<StationMarker> cluster, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        // Bitmap bitmap = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<StationMarker> cluster) {
        return cluster.getSize() > 1;
    }
}
