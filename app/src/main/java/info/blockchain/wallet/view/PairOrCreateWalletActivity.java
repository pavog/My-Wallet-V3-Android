package info.blockchain.wallet.view;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import info.blockchain.wallet.util.AppUtil;
import info.blockchain.wallet.viewModel.PairingViewModel;

import piuk.blockchain.android.BaseAuthActivity;
import piuk.blockchain.android.R;

public class PairOrCreateWalletActivity extends BaseAuthActivity {

    public static final int PAIRING_QR = 2005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);

        Fragment fragment;

        if (getIntent().getIntExtra("starting_fragment", 1) == 1) {
            fragment = new PairWalletFragment();
        } else {
            fragment = new CreateWalletFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    protected void startLogoutTimer() {
        // No-op
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
            new AppUtil(this).restartApp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PAIRING_QR) {
            if (data != null && data.getStringExtra(CaptureActivity.SCAN_RESULT) != null) {
                PairingViewModel viewModel = new PairingViewModel(PairOrCreateWalletActivity.this);
                viewModel.pairWithQR(data.getStringExtra(CaptureActivity.SCAN_RESULT));
            }
        }
    }
}
