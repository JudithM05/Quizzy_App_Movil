package com.example.quizzyappmobil.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.Pantallas.PantallaAjustes;
import com.example.quizzyappmobil.Pantallas.PantallaBuscar;
import com.example.quizzyappmobil.Pantallas.PantallaHome;
import com.example.quizzyappmobil.Pantallas.PantallaListas;
import com.example.quizzyappmobil.Pantallas.PantallaPerfil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class fragment_menu_inferior extends Fragment {

    public fragment_menu_inferior() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_inferior, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home_item) {
                navigateToActivity(PantallaHome.class);
                return true;
            } else if (itemId == R.id.buscar_item) {
                navigateToActivity(PantallaBuscar.class);
                return true;
            } else if (itemId == R.id.listas_item) {
                navigateToActivity(PantallaListas.class);
                return true;
            } else if (itemId == R.id.perfil_item) {
                navigateToActivity(PantallaPerfil.class);
                return true;
            } else if (itemId == R.id.ajustes_item) {
                navigateToActivity(PantallaAjustes.class);
                return true;
            }

            return false;
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        // Limpiar el stack de actividades para evitar acumulación
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Opcional: Añadir animación
        // requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
