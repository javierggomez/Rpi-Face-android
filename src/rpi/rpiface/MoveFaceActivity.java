package rpi.rpiface;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Copyright (C) 2013 Javier García, Julio Alberto González
 * <p>
 * This file is part of Rpi-Face. Rpi-Face is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * Rpi-Face is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * Rpi-Face. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * <p>
 * Esta clase se encarga de mostrar la interfaz para mover la cara a una
 * posición determinada y de recoger los métodos necesarios.
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class MoveFaceActivity extends Activity implements OnClickListener {

	/**
	 * Botón para poner contento
	 */
	private Button botonHappy;
	/**
	 * Botón para poner triste
	 */
	private Button botonSad;
	/**
	 * Botón para poner enfadado
	 */
	private Button botonAngry;
	/**
	 * Botón para poner sorprendido
	 */
	private Button botonSurprised;
	/**
	 * Botón para poner neutral
	 */
	private Button botonNeutral;
	/**
	 * Parámetro de la petición get
	 */
	private final String RPI_PARAM = "face";
	/**
	 * Etiqueta para el log
	 */
	private static final String LOGTAG = MoveFaceActivity.class
			.getCanonicalName();
	/**
	 * Preferencias del usuario
	 */
	private SharedPreferences preferences;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la vista activity_move
		setContentView(R.layout.activity_move);

		// Asigna a cada botón su correspondiente botón gráfico
		botonHappy = (Button) findViewById(R.id.button_happy);
		botonSad = (Button) findViewById(R.id.button_sad);
		botonAngry = (Button) findViewById(R.id.button_angry);
		botonSurprised = (Button) findViewById(R.id.button_surprised);
		botonNeutral = (Button) findViewById(R.id.button_neutral);

		// Pone detectores de clicks a cada botón
		botonHappy.setOnClickListener(this);
		botonSad.setOnClickListener(this);
		botonAngry.setOnClickListener(this);
		botonSurprised.setOnClickListener(this);
		botonNeutral.setOnClickListener(this);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Log.i(LOGTAG, "Actividad creada");
	}

	/**
	 * Código que se ejecuta cuando se hace click en un objeto con detector
	 * 
	 * @param v
	 *            Objeto sobre el cual se ha hecho click
	 */
	public void onClick(View v) {
		// Se comprueba el estado de la conexión
		ConnectionStatus cd = new ConnectionStatus(getApplicationContext());
		boolean internetConnection = cd.isConnectingToInternet();

		// Si no se está conectado se avisa y se sale del método
		if (!internetConnection) {
			Toast.makeText(getApplicationContext(),
					"No está conectado a internet.", Toast.LENGTH_SHORT).show();
			return;
		}
		// Código que se ejecuta si se está conectado
		switch (v.getId()) {
		case R.id.button_happy:
			// Si se ha pulsado el botón contento
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón contento");
			doGet(0);
			break;
		case R.id.button_sad:
			// Si se ha pulsado el botón triste
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón triste");
			doGet(1);
			break;
		case R.id.button_surprised:
			// Si se ha pulsado el botón sorprendido
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón sorprendido");
			doGet(2);
			break;
		case R.id.button_angry:
			// Si se ha pulsado el botón enfadado
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón enfadado");
			doGet(3);
			break;
		case R.id.button_neutral:
			// Si se ha pulsado el botón neutral
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón aburrido");
			doGet(4);
			break;
		default:
			// Si se ha pulsado otro botón
			Toast.makeText(getApplicationContext(),
					"Error, esa opción no está implementada",
					Toast.LENGTH_SHORT).show();
			Log.w(LOGTAG + " onclick", "Opción no contemplada");
			break;
		}

	}

	/**
	 ** Llama a un Asynctask que se encarga de hacer una petición get al
	 * servidor. Además comprueba que la conexión se haya hecho correctamente.
	 ** 
	 ** @param index
	 *            Número que indica el movimiento a realizar por la cara
	 */
	private void doGet(int index) {
		String value = Integer.toString(index);
		String rpi = preferences.getString(PreferencesActivity.PREFS_URL,
				Url.RPI);
		String rpiPort = preferences.getString(PreferencesActivity.PREFS_PORT,
				Url.RPI_PORT);
		String rpiPath = preferences.getString(PreferencesActivity.PREFS_PATH,
				Url.RPI_PATH);

		// Se crea un nuego getAsynctask para hacer la petición get.
		GetAsyncTask getAsyncTask = new GetAsyncTask(getApplicationContext());
		// Se ejecuta el nuevo asynctask
		getAsyncTask.execute(value, rpi, rpiPort, rpiPath, RPI_PARAM);

	}

	/**
	 * Se ejecuta cada vez que se llama al menú estando en la actividad
	 * 
	 * @param menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	/**
	 * Se ejecuta cuando se pulsa un botón del menu
	 * 
	 * @param item
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intentPrefs = new Intent(this, PreferencesActivity.class);
			startActivity(intentPrefs);
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
