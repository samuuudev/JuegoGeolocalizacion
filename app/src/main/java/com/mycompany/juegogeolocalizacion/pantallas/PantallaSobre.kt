package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R

@Composable
fun PantallaSobre(
    onVolver: () -> Unit = {}
){
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.sobre),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.titulo),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.desarrolladores),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(R.string.descripcion),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(R.string.version),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.volver))
        }
    }
}