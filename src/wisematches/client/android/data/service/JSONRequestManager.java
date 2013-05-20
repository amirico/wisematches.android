package wisematches.client.android.data.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import wisematches.client.android.data.DataRequestManager;
import wisematches.client.android.data.model.person.Personality;
import wisematches.client.android.data.model.scribble.ScribbleDescriptor;

import java.util.ArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class JSONRequestManager extends RequestManager implements DataRequestManager {
	public JSONRequestManager(Context context) {
		super(context, JSONRequestService.class);
	}

	@Override
	public void authenticate(String username, String password, DataResponse<Personality> response) {
		Request request = JSONRequestFactory.getAuthRequest(username, password);
		execute(request, new TheRequestListener<>(response));
	}

	@Override
	public void register(String nickname, String email, String password, String confirm, String language, String timezone, DataResponse<Personality> response) {
		Request request = JSONRequestFactory.getRegisterRequest(nickname, email, password, confirm, language, timezone);
		execute(request, new TheRequestListener<>(response));
	}

	@Override
	public void getActiveGames(long pid, DataResponse<ArrayList<ScribbleDescriptor>> response) {
		Request request = JSONRequestFactory.getActiveGamesRequest(pid);
		execute(request, new TheRequestListener<>(response));
	}

	private class TheRequestListener<T> implements RequestManager.RequestListener {
		private final DataResponse<T> response;

		protected TheRequestListener(DataResponse<T> response) {
			this.response = response;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onRequestFinished(Request request, Bundle resultData) {
			if (resultData == null) {
				Log.d("WM", "Finished: empty");
				response.onSuccess(null);
			} else {
				Log.d("WM", "Finished: not empty");
				final String type = resultData.getString(JSONRequestFactory.BUNDLE_EXTRA_RESPONSE_TYPE);
				if (type.equals(JSONRequestFactory.BUNDLE_EXTRA_RESPONSE_TYPE_LIST)) {
					response.onSuccess((T) resultData.getParcelableArrayList(JSONRequestFactory.BUNDLE_EXTRA_RESPONSE_TYPE_LIST));
				} else if (type.equals(JSONRequestFactory.BUNDLE_EXTRA_RESPONSE_TYPE_PRIMITIVE)) {
					response.onSuccess((T) resultData.getParcelable(JSONRequestFactory.BUNDLE_EXTRA_RESPONSE_TYPE_PRIMITIVE));
				}
			}
		}

		@Override
		public void onRequestDataError(Request request) {
			Log.d("WM", "Finished: onDataError");
			response.onDataError();
		}

		@Override
		public void onRequestConnectionError(Request request, int statusCode) {
			Log.d("WM", "Finished: onConnectionError " + statusCode);
			response.onConnectionError(statusCode);
		}

		@Override
		public void onRequestCustomError(Request request, Bundle resultData) {
			Log.d("WM", "Finished: onFailure");
			response.onFailure(resultData.getString("code"), resultData.getString("message"));
		}
	}
}