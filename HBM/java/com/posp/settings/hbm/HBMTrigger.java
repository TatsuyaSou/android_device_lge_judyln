package com.posp.settings.hbm;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.posp.settings.hardware.util.FileUtils;

import static com.posp.settings.hardware.util.FileUtils.fileExists;
import static com.posp.settings.hardware.util.FileUtils.isFileWritable;

public class HBMTrigger extends TileService {

    public String hbmtrigger = "/sys/devices/virtual/panel/brightness/hl_mode";
    public String TAG = "HBMTrigger";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called when the tile is added to the quick settings from the edit interface by the user. If
     * you keep track of added tiles, override this and update it.
     * <p>
     * Return either TILE_MODE_ACTIVE or TILE_MODE_PASSIVE depending on your requirements
     */
    @Override
    public void onTileAdded() {
        super.onTileAdded(); // in case the phone doesn't support the HBM Trigger or it doesn't have permission to edit it
        if (!fileExists(hbmtrigger) || !isFileWritable(hbmtrigger)) {
            setTileUnavailable();
        }
    }

    /**
     * Called when the tile is removed from the quick settings using the edit interface. Similarly
     * to onTileAdded, override this and update your tracking here if you need to
     */
    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        setTileInactive();
    }

    /**
     * Called when the tile is brought into the listening state. Update it with your icon and title
     * here, using getQsTile to get the tile (see below)
     */
    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    /**
     * Called when the tile is brought out of the listening state. This represents when getQsTile
     * will now return null.
     */
    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    /**
     * Called when the tile is clicked. Can be called multiple times in short succession, so double
     * click (and beyond) is possible
     */
    @Override
    public void onClick() {
        super.onClick();
        switch (ReadHBM()) {
            case "0":
                setTileActive();
                break;
            case "1":
                setTileInactive();
                break;
            default:
                Log.println(Log.ERROR, TAG, "Error writing to file" + hbmtrigger + " " + ReadHBM());
                setTileInactive();
        }
    }

    private void setTileActive()
    {
        Tile hbmtile = getQsTile();
        FileUtils.writeLine(hbmtrigger, "1");
        hbmtile.setState(Tile.STATE_ACTIVE);
        hbmtile.setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_brightness_7_24));
        //hbmtile.setSubtitle("Active");
        hbmtile.updateTile();
    }

    private void setTileInactive()
    {
        Tile hbmtile = getQsTile();
        FileUtils.writeLine(hbmtrigger, "0");
        //hbmtile.setSubtitle("Inactive");
        hbmtile.setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_brightness_5_24));
        hbmtile.setState(Tile.STATE_INACTIVE);
        hbmtile.updateTile();
    }

    private void setTileUnavailable()
    {
        Tile hbmtile = getQsTile();
        hbmtile.setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_highlight_off_24));
        hbmtile.setState(Tile.STATE_UNAVAILABLE);
        hbmtile.setLabel("HBM Unavailable");
        hbmtile.updateTile();
    }

    public String ReadHBM() {
        return FileUtils.readOneLine(hbmtrigger);
    }
}
