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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.WiFiNetwork;

public class ManualInputActivity extends Activity implements
        NetworksListFragment.OnItemSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.keygen_fragment,
                            ManualInputFragment
                                    .newInstance(getIntent()
                                            .getStringExtra(
                                                    ManualInputFragment.MAC_ADDRESS_ARG)))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this,
                        new Intent(this, NetworksListActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
                return true;
            case R.id.pref:
                startActivity(new Intent(this, Preferences.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences, menu);
        return true;
    }

    public void onItemSelected(WiFiNetwork wiFiNetwork) {
        if (wiFiNetwork.getSupportState() == Keygen.UNSUPPORTED) {
            Toast.makeText(this, R.string.msg_unspported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent detailIntent = new Intent(this, NetworkActivity.class);
        detailIntent.putExtra(NetworkFragment.NETWORK_ID, wiFiNetwork);
        startActivity(detailIntent);
    }

    public void onItemSelected(String mac) {
    }
}
