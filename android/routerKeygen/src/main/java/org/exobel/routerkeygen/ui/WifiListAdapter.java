/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exobel.routerkeygen.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.WiFiNetwork;

import java.util.ArrayList;

public class WifiListAdapter extends BaseAdapter implements
        PinnedSectionListView.PinnedSectionListAdapter {
    final private LayoutInflater inflater;
    private final Drawable[] wifiSignal;
    private final Drawable[] wifiSignalLocked;
    private final ArrayList<Item> listNetworks;
    private Typeface typeface = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WifiListAdapter(Context context) {
        this.listNetworks = new ArrayList<>();
        try {
            typeface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Light.ttf");
        } catch (Exception e) {
            // Rarely some devices have a problem creating this typeface
        }

        final Resources resources = context.getResources();
        inflater = LayoutInflater.from(context);
        wifiSignal = new Drawable[4];
        wifiSignalLocked = new Drawable[4];
        final int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        for (int i = 0; i < 4; ++i) {
            switch (i) {
                case 0:
                    wifiSignal[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_1_bar_black_24dp);
                    wifiSignalLocked[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_1_bar_lock_black_24dp);
                    break;
                case 1:
                    wifiSignal[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_2_bar_black_24dp);
                    wifiSignalLocked[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_2_bar_lock_black_24dp);
                    break;
                case 2:
                    wifiSignal[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_3_bar_black_24dp);
                    wifiSignalLocked[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_3_bar_lock_black_24dp);
                    break;
                case 3:
                    wifiSignal[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_4_bar_black_24dp);
                    wifiSignalLocked[i] = ContextCompat
                            .getDrawable(context, R.drawable.ic_signal_wifi_4_bar_lock_black_24dp);
                    break;
            }
            if (currentApiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                wifiSignal[i].setTint(resources.getColor(R.color.wifi_icons));
                wifiSignalLocked[i].setTint(resources.getColor(R.color.wifi_icons));
            } else{
                wifiSignal[i].setColorFilter(
                        resources.getColor(R.color.wifi_icons),
                        PorterDuff.Mode.SRC_IN
                );
                wifiSignalLocked[i].setColorFilter(
                        resources.getColor(R.color.wifi_icons),
                        PorterDuff.Mode.SRC_IN
                );
            }

        }
    }

    public int getCount() {
        return listNetworks.size();
    }

    public Item getItem(int position) {
        return listNetworks.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getCount())
            return -1;
        return getItem(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position < getCount() && getItem(position).type == Item.ITEM;
    }

    public long getItemId(int position) {
        return position;
    }

    private View getSectionView(ViewGroup parent) {
        final TextView view = (TextView) inflater.inflate(
                android.R.layout.simple_list_item_1, parent, false);
        if (typeface != null)
            view.setTypeface(typeface);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        view.setTextColor(0xFFFFFFFF);
        return view;
    }

    private View getItemView(ViewGroup parent) {
        final View convertView = inflater.inflate(R.layout.item_list_wifi,
                parent, false);
        final ViewHolder holder = new ViewHolder(
                (TextView) convertView.findViewById(R.id.wifiName),
                (TextView) convertView.findViewById(R.id.wifiMAC),
                (ImageView) convertView.findViewById(R.id.strenght));
        if (typeface != null)
            holder.ssid.setTypeface(typeface);
        holder.ssid.setSelected(true);
        holder.ssid.setEllipsize(TruncateAt.MARQUEE);
        if (typeface != null)
            holder.mac.setTypeface(typeface);
        holder.mac.setSelected(true);
        holder.mac.setEllipsize(TruncateAt.MARQUEE);
        convertView.setTag(holder);
        return convertView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Item wifi = getItem(position);
        if (convertView == null) {
            if (wifi.type == Item.ITEM) {
                convertView = getItemView(parent);
            } else {
                convertView = getSectionView(parent);
            }
        } else {
            switch (wifi.type) {
                case Item.ITEM:
                    if (convertView.getTag() == null) {
                        convertView = getItemView(parent);
                    }
                    break;
                case Item.SECTION:
                    if (convertView.getTag() != null) {
                        convertView = getSectionView(parent);
                    }
                    break;
            }
        }

        if (wifi.type == Item.ITEM) {
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder != null){
                holder.ssid.setText(wifi.wifiNetwork.getSsidName());
                holder.mac.setText(wifi.wifiNetwork.getMacAddress());
                final int strenght = wifi.wifiNetwork.getLevel();
                if (wifi.wifiNetwork.isLocked()) {
                    holder.networkStrength.setImageDrawable(getWifiSignalLocked(strenght));
                } else {
                    holder.networkStrength.setImageDrawable(getWifiSignal(strenght));
                }
            }
        } else {
            TextView view = (TextView) convertView;
            view.setText(wifi.text);
            view.setBackgroundColor(parent.getResources().getColor(wifi.color));
        }
        return convertView;
    }

    /**
     * Protected wifiSignalLocked
     * @param strenght
     * @return
     */
    protected Drawable getWifiSignalLocked(int strenght){
        return wifiSignalLocked[Math.max(0, Math.min(strenght, wifiSignalLocked.length - 1))];
    }

    /**
     * Protected getWifiSignal
     * @param strenght
     * @return
     */
    protected Drawable getWifiSignal(int strenght){
        return wifiSignal[Math.max(0, Math.min(strenght, wifiSignal.length - 1))];
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    public void updateNetworks(WiFiNetwork[] list) {
        if (list != null) {
            listNetworks.clear();
            int currentSupportState = -1;
            for (WiFiNetwork wifi : list) {
                if (wifi.getSupportState() != currentSupportState) {
                    currentSupportState = wifi.getSupportState();
                    switch (currentSupportState) {
                        case Keygen.SUPPORTED:
                            listNetworks.add(new Item(Item.SECTION,
                                    R.string.networklist_supported, null,
                                    R.color.green_dark));
                            break;
                        case Keygen.UNLIKELY_SUPPORTED:
                            listNetworks.add(new Item(Item.SECTION,
                                    R.string.networklist_unlikely_supported, null,
                                    R.color.orange_dark));
                            break;
                        case Keygen.UNSUPPORTED:
                            listNetworks.add(new Item(Item.SECTION,
                                    R.string.networklist_unsupported, null,
                                    R.color.red_dark));
                            break;
                    }
                }
                listNetworks.add(new Item(Item.ITEM, 0, wifi, 0));
            }
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        final private TextView ssid;
        final private TextView mac;
        final private ImageView networkStrength;

        public ViewHolder(TextView ssid, TextView mac, ImageView networkStrength) {
            this.ssid = ssid;
            this.mac = mac;
            this.networkStrength = networkStrength;
        }
    }

    public static class Item {
        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final int text;
        public final WiFiNetwork wifiNetwork;
        public final int color;

        public Item(int type, int text, WiFiNetwork wifiNetwork, int color) {
            this.type = type;
            this.text = text;
            this.wifiNetwork = wifiNetwork;
            this.color = color;
        }
    }
}
