package br.ufrn.dimap.pubshare.activity;

import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufrn.dimap.pubshare.domain.Profile;
import br.ufrn.dimap.pubshare.domain.User;
import br.ufrn.dimap.pubshare.restclient.results.AuthenticationResult;
import br.ufrn.dimap.pubshare.restclient.results.UserResult;
import br.ufrn.dimap.pubshare.util.Constants;
import br.ufrn.dimap.pubshare.util.SessionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends PubnotesActivity {
	private ProgressDialog dialog;
	AsyncTask<User, Void, UserResult> async;
	// Session Manager Class
	//usar sempre que precisar pegar info do usuario logado atualmente
    SessionManager session;
	EditText e1;
	EditText e2;
	EditText e3;
	EditText e4;
	//Personal Information
	EditText e5;
	EditText e6;
	//Contact information
	EditText e7;
	EditText e9;

	//Profile profile;
	User userlogado;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		 // Session class instance
        session = new SessionManager(getApplicationContext());
		
		//Basic information
        e1 = (EditText) findViewById(R.id.einstitution);
		e2 = (EditText) findViewById(R.id.edegree);
		e3 = (EditText) findViewById(R.id.ecountry);
		e4 = (EditText) findViewById(R.id.egender);
		//Personal Information
		e5 = (EditText) findViewById(R.id.ebirthday);
		e6 = (EditText) findViewById(R.id.eaboutme);
		//Contact information
		e7 = (EditText) findViewById(R.id.efacebook);
		e9 = (EditText) findViewById(R.id.ephone);
		
		TextView usernamet = (TextView) findViewById(R.id.title);
		TextView about = (TextView) findViewById(R.id.artist);
		
		userlogado = EditProfileActivity.this.getCurrentUser();
		
		usernamet.setText(userlogado.getUsername());
		about.setText(userlogado.getUserprofile().getAboutme());
		
		e1.setText(userlogado.getUserprofile().getInstitution());
		e2.setText(userlogado.getUserprofile().getDegree());
		e3.setText(userlogado.getUserprofile().getLocation());
		e4.setText(userlogado.getUserprofile().getGender());
		e5.setText(userlogado.getUserprofile().getBirthday());
		e6.setText(userlogado.getUserprofile().getAboutme());
		e7.setText(userlogado.getUserprofile().getFacebook());
		e9.setText(userlogado.getUserprofile().getPhone());
		
		Log.d("INFO USER ATUAL", "useremail " + EditProfileActivity.this.getCurrentUser().getUseremail());
		Log.d("INFO USER ATUAL", "password " + EditProfileActivity.this.getCurrentUser().getPassword());
		
		findViewById(R.id.btnEditDone).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						//Aqui devo pegar todas as informacoes, criar um Profile
						//profile = new Profile();
						userlogado.getUserprofile().setId(userlogado.getUserprofile().getId());
						userlogado.getUserprofile().setInstitution(e1.getText().toString());
						userlogado.getUserprofile().setDegree(e2.getText().toString());
						userlogado.getUserprofile().setLocation(e3.getText().toString());
						userlogado.getUserprofile().setGender(e4.getText().toString());
						userlogado.getUserprofile().setBirthday(e5.getText().toString());
						userlogado.getUserprofile().setAboutme(e6.getText().toString());
						userlogado.getUserprofile().setFacebook(e7.getText().toString());
						userlogado.getUserprofile().setPhone(e9.getText().toString());
						
						//userlogado.setUserprofile(profile);
						
						//EditProfileActivity.this.setCurrentUser(userlogado);
						
						//acessar o servidor atraves do (id) e atualizar o user lah
						async.execute(userlogado);
						Intent i = new Intent(EditProfileActivity.this, ShowProfileActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
				}); 
		
		/** creating the new asyncTask here **/
		async = new AsyncTask<User, Void, UserResult>(){
			
			
			protected void onPreExecute() {
				//dialog = new ProgressDialog(EditProfileActivity.this);
				super.onPreExecute();
				//dialog.setMessage("Updating profile...");
				//dialog.show();
			}
			
			protected UserResult doInBackground(User... user) {
				return saveProfile(user[0]);
				
			}
			
			/** now lets update the interface **/
			protected void onPostExecute(UserResult result) {
				/*if(dialog.isShowing())
				{
					dialog.dismiss();
				}*/
				Toast.makeText(EditProfileActivity.this,
						"Updated profile!", Toast.LENGTH_SHORT).show();
				
			}
		};		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	private UserResult saveProfile(User user)
	{
		HttpHeaders requestHeaders = new HttpHeaders(); 
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		 
		RestTemplate restTemplate = new RestTemplate();
	
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		
		String url = "/user/saveProfile";
		
		ResponseEntity<UserResult> response = restTemplate.exchange(  
				Constants.URL_SERVER + url, 
				HttpMethod.POST, 
				new HttpEntity<Object>(user, requestHeaders), UserResult.class);
		
		
		UserResult result = response.getBody();
				
		return result;
	}
}
