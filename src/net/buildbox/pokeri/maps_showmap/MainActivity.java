package net.buildbox.pokeri.maps_showmap;

import android.location.Location;
//--------------------------------------------------------------------------------------------
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
//--------------------------------------------------------------------------------------------
//現在地表示関連

//yoshidaddd

public class MainActivity extends FragmentActivity implements
	ConnectionCallbacks,
	OnConnectionFailedListener,
	LocationListener,
	OnMyLocationButtonClickListener {

//--------------------------------------------------------------------------------------------
//	クラス内共用変数の作成

	private FragmentManager fragmentManager;
	private SupportMapFragment fragment;
	private GoogleMap map;
	private LatLng posMapPoint;
	private CameraPosition.Builder builder;
	private MarkerOptions options;
	private Marker marker;
	private int mflg = 0;

    private LocationClient mLocationClient;

//--------------------------------------------------------------------------------------------
//	マップ関連の処理
	/**
	 * 4つのピンを立てる
	 * ピンを立てて囲った範囲の色を変える
	 * ぐるなび等のAPIと連携して検索する
	 * 検索した場所にピンを立てる
	 */

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fragmentManager = getSupportFragmentManager();
		fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentMap);

		// GoogleMapのインスタンス取得
		map = fragment.getMap();
		// 表示位置（東京駅）の生成
		posMapPoint = new LatLng(35.681382, 139.766084);
		// 東京駅を表示
		builder = new CameraPosition.Builder();
		// ピンの設定
		options = new MarkerOptions();

		// 現在地取得を許可
	    map.setMyLocationEnabled(true);
	    // 現在地ボタンタッチイベントを取得する
	    map.setOnMyLocationButtonClickListener(this);
	    // Location Serviceを使用するため、LocationClientクラスのインスタンスを生成する
        mLocationClient = new LocationClient(
                getApplicationContext(),
                this,  // ConnectionCallbacks
                this); // OnConnectionFailedListener

		MapsInitializer.initialize(this);

		// 東京駅を表示
		builder.target(posMapPoint);	// カメラの表示位置の指定
		builder.zoom(13.0f);	// カメラのズームレベルの指定
		builder.bearing(0);		// カメラの向きの指定
		builder.tilt(25.0f);	// カメラの傾きの指定
		map.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

		// ピンの設定
//		options.position(posMapPoint);
//		options.title("東京駅");

		// ピンの追加
//		marker = map.addMarker(options);

		// ピン上の情報がクリックされた時のイベント処理
//		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//			@Override
//			public void onInfoWindowClick(Marker marker) {
//				Toast.makeText(getApplicationContext(), "東京駅がクリックされました。", Toast.LENGTH_SHORT).show();
//			}
//		});

		// マップ上のクリックイベント処理
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
		    @Override
		    public void onMapClick(LatLng point) {
		        Toast.makeText(getApplicationContext(),
		        		"クリックされた座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
		    }


		});

		// マップ上の長押しイベント処理
		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
		    @Override
		    public void onMapLongClick(LatLng point) {
		    	Toast.makeText(getApplicationContext(),
		        		"長押しされた座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();

		    	// 既に4つのマーカーが表示されていればマーカーを生成しない
		    	if (mflg < 4) {
			    	// 表示位置（長押しされた座標）の生成
			    	posMapPoint = new LatLng(point.latitude, point.longitude);
					// ピンとタイトルの設定
					options.position(posMapPoint);
					options.title("長押しされたポイント");
					// ピンの追加
					marker = map.addMarker(options);
					// ピンを長押しでドラッグ可能に
					marker.setDraggable(true);
					// ピン数のカウントを追加
					mflg++;
		    	}

		    }
		});



		// カメラの状態変化イベント処理
//		map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//		    @Override
//		    public void onCameraChange(CameraPosition position) {
//		        LatLng point = position.target;
//		        Toast.makeText(getApplicationContext(),
//		        		"カメラの状態が変化した座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
//		    }
//		});

//		↓マップのズーム倍率に上限値を与えている
//		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

//--------------------------------------------------------------------------------------------
//		スピナーの設定
		/**
		 * 検索ボックスを用意する（アクションバー？）
		 * ぐるなび等のAPIにキーワードとピンの座標を引き渡す
		 */

		String[] items = {"居酒屋", "カフェ", "観光スポット"};

		Spinner spinnerGenre = (Spinner) findViewById(R.id.spinnerGenre);
		// アダプタにアイテムを追加
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item,
				items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// アダプタの設定
		spinnerGenre.setAdapter(adapter);
		// スピナーのタイトル設定
		spinnerGenre.setPrompt("ジャンルの選択");
		// ポジションの指定
		spinnerGenre.setSelection(0);

		spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Spinner spnGenre = (Spinner) parent;
				String item = (String) spnGenre.getItemAtPosition(position);

		    	Toast.makeText(getApplicationContext(),"選択されたアイテムは " + item, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//--------------------------------------------------------------------------------------------
//		その他
		/**
		 * エリア設定のリセット
		 *
		 */

	}

	 // Implementation of {@link LocationListener}.
    @Override
    public void onLocationChanged(Location location) {
     // Do nathing
    }
    // Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(REQUEST,this);  // this = LocationListener
    }
    // Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
    @Override
    public void onDisconnected() {
        // Do nothing
    }
    // Implementation of {@link OnConnectionFailedListener}.
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

}