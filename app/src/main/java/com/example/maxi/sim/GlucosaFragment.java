package com.example.maxi.sim;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GlucosaFragment extends Fragment {

    private Button   btnGuardar;
    private TextView txtPaciente;
    private Paciente pacienteActivo;
    private EditText TxtGlucosa;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView  = inflater.inflate(R.layout.fragment_glucosa, container, false);
        fragmentActivo fragActivo =  fragmentActivo.getInstance();
        fragActivo.setData("GLUCOSA");

        txtPaciente = (TextView) rootView.findViewById(R.id.txtPaciente);
        TxtGlucosa = (EditText) rootView.findViewById(R.id.TxtGlucosa);
        pacienteActivo = (Paciente)getArguments().getSerializable("PACIENTE");
        txtPaciente.setText(pacienteActivo.getNombre()+" "+pacienteActivo.getApellido());


        btnGuardar = (Button) rootView.findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String vGlucosa =  TxtGlucosa.getText().toString();
                if(!vGlucosa.equals("")) {
                    Dialogo dialogo = new Dialogo();
                    Bundle bundle = new Bundle();
                    bundle.putString("TITULO", "Nivel de Glucosa fuera de rango");
                    bundle.putString("MENSAJE", "Se recomienda administrar XXmmg de insulina,Desea enviar un alerta?");

                    dialogo.setArguments(bundle);
                    dialogo.show(getFragmentManager(), "Nivel");

                    StringBuilder datosJson = new StringBuilder();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());

                    String IdlibroReport = "\"" + "idLibroReport" + "\"" + ":" + "5";
                    String glucosaJson = "\"" + "glucosa" + "\"" + ":"+vGlucosa;
                    String fechaJson = "\"" + "fecha" + "\"" + ":" + "\"" + "Oct 10, 2015 9:24:43 PM" + "\"";
                    //String fechaJson = "\"" + "fecha" + "\"" + ":" + "\"" + currentDateandTime.toString() + "\"";
                    String descJson = "\"" + "descripcion" + "\"" + ":" + "\"" + "nivel glucosa" + "\"";

                    datosJson.append("{");
                    datosJson.append(IdlibroReport);
                    datosJson.append(",");
                    datosJson.append("medicions");
                    datosJson.append("{");
                    datosJson.append(fechaJson);
                    datosJson.append(",");
                    datosJson.append(descJson);
                    datosJson.append(",");
                    datosJson.append(glucosaJson);
                    datosJson.append("}}");

                    System.out.println(datosJson.toString());

                    try {
                        postGlucosa(rootView.getContext(),datosJson);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast toast = Toast.makeText(rootView.getContext(),
                            "El campo de nivel de glucosa esta vacio", Toast.LENGTH_LONG);

                    toast.show();
                }

            }});
        return  rootView;
    }

    private void postGlucosa(Context farmacoContext, StringBuilder datos) throws IOException {

        SimWebService service = new SimWebService();

        if (service.validarConexion(farmacoContext)) {
            System.out.println("Red disponible");

            service.configurarMetodo("POST");
            service.configurarUrl("http://192.168.0.3:8080/simWebService/resources/MedicionResource");

            if (service.conectar(farmacoContext,datos.toString().getBytes().length)) {
                System.out.println("Datos "+"\n"+datos);
                service.post(datos.toString());
                System.out.println("-------------");

            }
        }
        else{
            System.out.println("Red No disponible");
            Toast toast = Toast.makeText(farmacoContext,"Red No Disponible",Toast.LENGTH_LONG);
            toast.show();
        }
    }




}
