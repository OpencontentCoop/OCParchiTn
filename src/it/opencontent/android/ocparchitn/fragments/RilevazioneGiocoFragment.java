package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Marco Albarelli <info@marcoalbarelli.eu>
 */

public class RilevazioneGiocoFragment extends Fragment implements ICustomFragment{

	private static final String TAG = RilevazioneGiocoFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_main, container, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(MainActivity.getCurrentGioco() != null){
			showStrutturaData(MainActivity.getCurrentGioco());
		}
	}


	public void salvaModifiche(View v){
		Log.d(TAG,"salvamodifiche nel fragment");
		saveLocal(MainActivity.getCurrentGioco());
	}
	
	private void updateText(int viewId, String text){
		TextView t = (TextView) getActivity().findViewById(viewId);
		if(t != null){
			t.setText(text);
		}
	}
	
	public void editMe(View v){
		Log.d(TAG,"editme nel fragment");	
		TextView t = (TextView) v;
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Modifica il dato");
		alert.setMessage("ID");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setText(t.getText());
		input.setTag(R.integer.tag_view_id, v.getId());
		
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				int viewId;
				try {
					viewId = Integer.parseInt(input.getTag(R.integer.tag_view_id).toString());
					updateText(viewId,value);
					Gioco g = MainActivity.getCurrentGioco();
					switch(viewId){
					case R.id.display_gioco_nota:
						g.note = value;
						break;
					case R.id.display_gioco_gpsx:
						g.gpsx = Float.parseFloat(value);
						break;
					case R.id.display_gioco_gpsy:
						g.gpsy = Float.parseFloat(value);
						break;
					}
					saveLocal(g);
				} catch (NumberFormatException nfe) {

				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();		
	}
	public void placeMe(View v){
		Gioco g = MainActivity.getCurrentGioco();
		g.gpsx = MainActivity.getCurrentLon();
		g.gpsy = MainActivity.getCurrentLat();
		g.hasDirtyData=true;
		g.sincronizzato = false;
		saveLocal(g);
	}
	public void showError(HashMap<String,String> map){
//		TextView errorView = (TextView) getActivity().findViewById(R.id.display_gioco_warning);
//		errorView.setText("");
//		Iterator<Entry<String,String>> i = map.entrySet().iterator();
//		while(i.hasNext()){
//			Entry<String,String> n = (Entry<String, String>) i.next();
//			errorView.append("\n"+n.getValue());
//		}
	}

	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		switch (requestCode) {

		
		default:
			break;
		}
	}
	
	private long saveLocal(Gioco gioco){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		long id = db.salvaStrutturaLocally(gioco);
		if(id > 0){
			Toast.makeText(getActivity().getApplicationContext(),"Gioco salvato localmente", Toast.LENGTH_SHORT).show();
			MainActivity ma = (MainActivity) getActivity();
			ma.updateCountDaSincronizzare();
		} else if (id == -2){
			//constraint error
		}
		return id;
	}
	

	public void showStrutturaData(Struttura gioco) {
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_gioco_id);
		v.setText(""+gioco.idGioco);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_marca);
		v.setText(gioco.descrizioneMarca);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_descrizione);
		v.setText(gioco.descrizioneGioco);
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(gioco.descrizioneArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(gioco.note);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_seriale);
		v.setText(gioco.numeroSerie);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText(gioco.rfid+"");
		if(gioco.rfid>0){ //&& !possiamoModificareGliRFID
			Button b = (Button) getActivity().findViewById(R.id.pulsante_associa_rfid_a_gioco);
					if(b!= null){
						b.setEnabled(false);
					}
		}
		if(gioco.rfid<=0){ //&& !possiamoModificareGliRFID
			Button b = (Button) getActivity().findViewById(R.id.pulsante_salva_modifiche_gioco);
					if(b!= null){
						b.setEnabled(false);
					}
		} else {
			Button b = (Button) getActivity().findViewById(R.id.pulsante_salva_modifiche_gioco);
			if(b!= null){
				b.setEnabled(true);
			}			
		}
		if(gioco.rfidArea>0){ //&& !possiamoModificareGliRFID
			Button b = (Button) getActivity().findViewById(R.id.pulsante_associa_rfid_a_area);
					if(b!= null){
						b.setEnabled(false);
					}
		}
		
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(gioco.rfidArea+"");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(gioco.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(gioco.gpsy + "");
		setupSnapshots(gioco);
	}
	

	private void setupSnapshots(Struttura gioco) {
		int width = 150;
		int height= 150;
		ImageView v;
		for (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) {
			switch(i){
			case 0:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto0, Base64.DEFAULT),getResources(),1,width,height));
				break;
			case 1:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto1, Base64.DEFAULT),getResources(),2,width,height));
				break;
			
			case 2:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_2);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto2, Base64.DEFAULT),getResources(),3,width,height));
				break;
				
			case 3:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_3);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto3, Base64.DEFAULT),getResources(),4,width,height));
				break;
				
			case 4:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_4);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto4, Base64.DEFAULT),getResources(),5,width,height));
				break;
				
			
			}
			
		}
	}

	@Override
	public void clickedMe(View v) {
		// TODO Auto-generated method stub
		
	}
	
	

}