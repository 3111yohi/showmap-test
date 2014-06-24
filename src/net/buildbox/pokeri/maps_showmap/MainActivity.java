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
//���ݒn�\���֘A

//yoshidaddd

public class MainActivity extends FragmentActivity implements
	ConnectionCallbacks,
	OnConnectionFailedListener,
	LocationListener,
	OnMyLocationButtonClickListener {

//--------------------------------------------------------------------------------------------
//	�N���X�����p�ϐ��̍쐬

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
//	�}�b�v�֘A�̏���
	/**
	 * 4�̃s���𗧂Ă�
	 * �s���𗧂ĂĈ͂����͈͂̐F��ς���
	 * ����Ȃѓ���API�ƘA�g���Č�������
	 * ���������ꏊ�Ƀs���𗧂Ă�
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

		// GoogleMap�̃C���X�^���X�擾
		map = fragment.getMap();
		// �\���ʒu�i�����w�j�̐���
		posMapPoint = new LatLng(35.681382, 139.766084);
		// �����w��\��
		builder = new CameraPosition.Builder();
		// �s���̐ݒ�
		options = new MarkerOptions();

		// ���ݒn�擾������
	    map.setMyLocationEnabled(true);
	    // ���ݒn�{�^���^�b�`�C�x���g���擾����
	    map.setOnMyLocationButtonClickListener(this);
	    // Location Service���g�p���邽�߁ALocationClient�N���X�̃C���X�^���X�𐶐�����
        mLocationClient = new LocationClient(
                getApplicationContext(),
                this,  // ConnectionCallbacks
                this); // OnConnectionFailedListener

		MapsInitializer.initialize(this);

		// �����w��\��
		builder.target(posMapPoint);	// �J�����̕\���ʒu�̎w��
		builder.zoom(13.0f);	// �J�����̃Y�[�����x���̎w��
		builder.bearing(0);		// �J�����̌����̎w��
		builder.tilt(25.0f);	// �J�����̌X���̎w��
		map.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

		// �s���̐ݒ�
//		options.position(posMapPoint);
//		options.title("�����w");

		// �s���̒ǉ�
//		marker = map.addMarker(options);

		// �s����̏�񂪃N���b�N���ꂽ���̃C�x���g����
//		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//			@Override
//			public void onInfoWindowClick(Marker marker) {
//				Toast.makeText(getApplicationContext(), "�����w���N���b�N����܂����B", Toast.LENGTH_SHORT).show();
//			}
//		});

		// �}�b�v��̃N���b�N�C�x���g����
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
		    @Override
		    public void onMapClick(LatLng point) {
		        Toast.makeText(getApplicationContext(),
		        		"�N���b�N���ꂽ���W�� " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
		    }


		});

		// �}�b�v��̒������C�x���g����
		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
		    @Override
		    public void onMapLongClick(LatLng point) {
		    	Toast.makeText(getApplicationContext(),
		        		"���������ꂽ���W�� " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();

		    	// ����4�̃}�[�J�[���\������Ă���΃}�[�J�[�𐶐����Ȃ�
		    	if (mflg < 4) {
			    	// �\���ʒu�i���������ꂽ���W�j�̐���
			    	posMapPoint = new LatLng(point.latitude, point.longitude);
					// �s���ƃ^�C�g���̐ݒ�
					options.position(posMapPoint);
					options.title("���������ꂽ�|�C���g");
					// �s���̒ǉ�
					marker = map.addMarker(options);
					// �s���𒷉����Ńh���b�O�\��
					marker.setDraggable(true);
					// �s�����̃J�E���g��ǉ�
					mflg++;
		    	}

		    }
		});



		// �J�����̏�ԕω��C�x���g����
//		map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//		    @Override
//		    public void onCameraChange(CameraPosition position) {
//		        LatLng point = position.target;
//		        Toast.makeText(getApplicationContext(),
//		        		"�J�����̏�Ԃ��ω��������W�� " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
//		    }
//		});

//		���}�b�v�̃Y�[���{���ɏ���l��^���Ă���
//		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

//--------------------------------------------------------------------------------------------
//		�X�s�i�[�̐ݒ�
		/**
		 * �����{�b�N�X��p�ӂ���i�A�N�V�����o�[�H�j
		 * ����Ȃѓ���API�ɃL�[���[�h�ƃs���̍��W�������n��
		 */

		String[] items = {"������", "�J�t�F", "�ό��X�|�b�g"};

		Spinner spinnerGenre = (Spinner) findViewById(R.id.spinnerGenre);
		// �A�_�v�^�ɃA�C�e����ǉ�
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item,
				items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// �A�_�v�^�̐ݒ�
		spinnerGenre.setAdapter(adapter);
		// �X�s�i�[�̃^�C�g���ݒ�
		spinnerGenre.setPrompt("�W�������̑I��");
		// �|�W�V�����̎w��
		spinnerGenre.setSelection(0);

		spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Spinner spnGenre = (Spinner) parent;
				String item = (String) spnGenre.getItemAtPosition(position);

		    	Toast.makeText(getApplicationContext(),"�I�����ꂽ�A�C�e���� " + item, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//--------------------------------------------------------------------------------------------
//		���̑�
		/**
		 * �G���A�ݒ�̃��Z�b�g
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