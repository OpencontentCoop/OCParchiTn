package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ControlloFragment extends Fragment implements ICustomFragment {

	private static final String TAG = ControlloFragment.class.getSimpleName();
	
	public static int tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
	public static String methodName = Constants.GET_GIOCO_METHOD_NAME;
	public static int soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
	public static int tipoControllo = Constants.CODICE_STRUTTURA_CONTROLLO_VISIVO;
	private Controllo controllo;

	/**
	 * Il primo controlo sullo stack sarà sempre quello attivo
	 * cui faranno riferimento i metodi delle altre classi
	 * Se la lista è vuota ne sarà creato uno
	 */
	private static List<Controllo> elencoControlli = new ArrayList<Controllo>();
	
	public static void appendControllo(Controllo controllo){
		elencoControlli.add(controllo);
	}
	
	public static void aggiungiSnapshotAControlloCorrente(String base64){
		if(elencoControlli.size()>0){
			elencoControlli.get(0).foto = base64;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.controllo_fragment, container, false);
		controllo = new Controllo();
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		disableRadioButtons();
	}

	@Override
	public void salvaModifiche(View v) {
		Log.d(TAG,"salva modifiche alla periodica");
		if(elencoControlli.size()>0){
			long id = saveLocal(elencoControlli.get(0));
			if(id>0){
				elencoControlli.remove(0);
			}
		}
	}
	private long saveLocal(Controllo c){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		long id = db.salvaControlloLocally(c);
		if(id > 0){
			Toast.makeText(getActivity().getApplicationContext(),"Operazione salvata localmente", Toast.LENGTH_SHORT).show();
			MainActivity ma = (MainActivity) getActivity();
			ma.updateCountDaSincronizzare();
		} else if (id == -2){
			//constraint error
		}
		
		return id;
	}
	public void editMe(View v){
		Log.d(TAG,"editme nel fragment");	
		switch(v.getId()){
		case R.id.display_area_tipoPavimentazione:
			break;
		default: 
			TextView t = (TextView) v;
			changeTextValueThroughAlert(t);
			break;
		}
	}


	private void changeTextValueThroughAlert(TextView t) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Modifica il dato");
		alert.setMessage("Controllo: ");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setText(t.getText());
		input.setTag(R.integer.tag_view_id, t.getId());
		final int viewId =Integer.parseInt(input.getTag(R.integer.tag_view_id).toString());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				try {
					switch(viewId){
					case R.id.display_controllo_nota:
						if(elencoControlli.size()>0){
							elencoControlli.get(0).noteControllo =value;
							saveLocal(elencoControlli.get(0));
							updateText(viewId,value);
						}
						break;
					}
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
	
	private void updateText(int viewId, String text){
		TextView t = (TextView) getActivity().findViewById(viewId);
		if(t != null){
			t.setText(text);
		}
	}	
	
	private void displayGioco(Gioco gioco){
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
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText(((Gioco) gioco).posizioneRfid);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText(gioco.rfid+"");
		
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(gioco.rfidArea+"");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(gioco.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(gioco.gpsy + "");		
		
		
	}
	private void displayArea(Area area){
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_area_id);
		v.setText(""+area.idArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(area.note);
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(area.rfidArea+"");
		
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(area.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(area.gpsy + "");
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(area.descrizioneArea + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText(area.posizioneRfid + "");
		
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		RecordTabellaSupporto tipoPavimentazione = db.tabelleSupportoGetRecord(Constants.TABELLA_TIPO_PAVIMENTAZIONI, area.tipoPavimentazione);
		
		v  = (TextView) getActivity().findViewById(R.id.display_area_tipoPavimentazione_fissa);
		v.setText(tipoPavimentazione.descrizione);
	}
	
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		if(struttura.getClass().equals(Gioco.class)){
			displayGioco((Gioco) struttura);
		}	else  if(struttura.getClass().equals(Area.class)){
			displayArea((Area) struttura);
		}
		setupSnapshots(struttura);
	}
	
	private void setupSnapshots(Struttura struttura) {
		int width = 100;
		int height= 100;
		ImageView v;
		for (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) {
			switch(i){
			case 0:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto0, Base64.DEFAULT),getResources(),1,width,height));
				break;
			case 1:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto1, Base64.DEFAULT),getResources(),2,width,height));
				break;
			
			case 2:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_2);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto2, Base64.DEFAULT),getResources(),3,width,height));
				break;
				
			case 3:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_3);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto3, Base64.DEFAULT),getResources(),4,width,height));
				break;
				
			case 4:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_4);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto4, Base64.DEFAULT),getResources(),5,width,height));
				break;
			}
		}
	}	

	/**
	 * 
	 */
	private void disableRadioButtons() {
		RadioButton rb;
		rb = (RadioButton) getActivity().findViewById(R.id.radio_intervento);
		if(rb!=null){
			rb.setEnabled(false);
		}
		rb = (RadioButton) getActivity().findViewById(R.id.radio_manutenzione);
		if(rb!=null){
			rb.setEnabled(false);
		}
		rb = (RadioButton) getActivity().findViewById(R.id.radio_verifica);
		if(rb!=null){
			rb.setEnabled(false);
		}
	}	
	@Override
	public void clickedMe(View v) {
		switch(v.getId()){
		case R.id.radio_gioco:
			tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
			methodName = Constants.GET_GIOCO_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
			break;
		case R.id.radio_area:
			tipoStruttura = Constants.CODICE_STRUTTURA_AREA;
			methodName = Constants.GET_AREA_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID;
			break;
		case R.id.radio_controllo:
			tipoControllo = Constants.CODICE_STRUTTURA_CONTROLLO_VISIVO;
			break;
		case R.id.radio_verifica:
			tipoControllo = Constants.CODICE_STRUTTURA_VERIFICA;
			break;
		case R.id.radio_intervento:
			tipoControllo = Constants.CODICE_STRUTTURA_INTERVENTO;
			break;
		case R.id.radio_manutenzione:
			tipoControllo = Constants.CODICE_STRUTTURA_MANUTENZIONE;
			break;
			
		}
	}	
}