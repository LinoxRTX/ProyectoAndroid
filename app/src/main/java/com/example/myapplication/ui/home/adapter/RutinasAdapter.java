package com.example.myapplication.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.RutinaModel;
import java.util.ArrayList;
import java.util.List;

// La interfaz OnRutinaClickListener ya está en su propio archivo.

// El Adaptador para el RecyclerView
public class RutinasAdapter extends RecyclerView.Adapter<RutinasAdapter.RutinaViewHolder> {

    private List<RutinaModel> rutinas = new ArrayList<>();
    private final OnRutinaClickListener listener;

    // El constructor recibe el "listener" (quien escuchará los clics)
    public RutinasAdapter(OnRutinaClickListener listener) {
        this.listener = listener;
    }

    // 1. Crea la vista de la fila (Infla el item_rutina.xml)
    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    // 2. Rellena la vista con datos (Binding)
    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        RutinaModel rutinaActual = rutinas.get(position);
        holder.bind(rutinaActual, listener);
    }

    // 3. Devuelve cuántos items hay en la lista
    @Override
    public int getItemCount() {
        return rutinas.size();
    }

    // Método para actualizar la lista de rutinas desde el ViewModel
    public void setRutinas(List<RutinaModel> nuevasRutinas) {
        this.rutinas = nuevasRutinas;
        notifyDataSetChanged(); // Refresca la lista
    }

    // --- El ViewHolder ---
    // Representa una sola fila (item_rutina.xml) y sus vistas
    class RutinaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombreRutina;
        private TextView tvDiasRutina;
        private TextView tvRutinaId;
        private ImageButton btnEditar;
        private ImageButton btnEliminar;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Conectar las vistas del XML
            tvNombreRutina = itemView.findViewById(R.id.tvNombreRutina);
            tvDiasRutina = itemView.findViewById(R.id.tvDiasRutina);
            tvRutinaId = itemView.findViewById(R.id.tvRutinaId);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        // Método "bind" para rellenar los datos y asignar clics
        public void bind(final RutinaModel rutina, final OnRutinaClickListener listener) {
            tvNombreRutina.setText(rutina.getNombreRutina());
            tvDiasRutina.setText(rutina.getDiasDeRutina());
            tvRutinaId.setText(rutina.getId()); // ID Oculto (para referencia)

            // Asignar los clics a los botones
            btnEditar.setOnClickListener(v -> listener.onEditarClick(rutina));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(rutina));

            // --- LÍNEA AÑADIDA (LA QUE FALTABA) ---
            // Asigna el clic a TODA la tarjeta (itemView) para ver los detalles
            itemView.setOnClickListener(v -> listener.onRutinaClick(rutina));
        }
    }
}