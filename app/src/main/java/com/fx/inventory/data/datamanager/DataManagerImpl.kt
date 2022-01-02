package com.fx.inventory.data.datamanager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class DataManagerImpl constructor(var context: Context):DataManager{


    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user-data")

    override fun getAuthToken(): Flow<String?> {
             return  context.dataStore.data.map {
              it[stringPreferencesKey(TOKEN)]
            }
        }


    override fun setAuthToken(authToken: String?) {
        CoroutineScope(IO).launch {
            context.dataStore.edit {preferences->
                val  key = stringPreferencesKey(TOKEN)
                preferences[key] =  authToken!!;
            }
        }

    }

    override fun testInjection() {
        Log.e(TAG, "setAuthToken: Setting auth ...", )
    }

    companion object {
        private const val TAG = "DataManagerImpl"
        const val TOKEN = "token"
    }

}