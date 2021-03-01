package com.keep.alive.tile;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(Build.VERSION_CODES.N)
public class OhTileService extends TileService {
    private static final String TAG = "OH_TILE_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onClick() {
        super.onClick();
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
        Log.d(TAG, "onClick()");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(TAG, "onStartListening");
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(TAG, "onTileAdded()");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(TAG, "onTileRemoved()");
    }
}
