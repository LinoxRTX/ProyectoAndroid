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
public class RutinasAdapter extends RecyclerView.Adapter<RutinasAdapter.RutinaViewHolder> {

    private List<RutinaModel> rutinas = new ArrayList<>();
    private final OnRutinaClickListener listener;
    public RutinasAdapter(OnRutinaClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        RutinaModel rutinaActual = rutinas.get(position);
        holder.bind(rutinaActual, listener);
    }
    @Override
    public int getItemCount() {
        return rutinas.size();
    }
    public void setRutinas(List<RutinaModel> nuevasRutinas) {
        this.rutinas = nuevasRutinas;
        notifyDataSetChanged();
    }
    class RutinaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreRutina;
        private TextView tvDiasRutina;
        private TextView tvRutinaId;
        private ImageButton btnEditar;
        private ImageButton btnEliminar;
        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRutina = itemView.findViewById(R.id.tvNombreRutina);
            tvDiasRutina = itemView.findViewById(R.id.tvDiasRutina);
            tvRutinaId = itemView.findViewById(R.id.tvRutinaId);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
        public void bind(final RutinaModel rutina, final OnRutinaClickListener listener) {
            tvNombreRutina.setText(rutina.getNombreRutina());
            tvDiasRutina.setText(rutina.getDiasDeRutina());
            tvRutinaId.setText(rutina.getId());
            btnEditar.setOnClickListener(v -> listener.onEditarClick(rutina));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(rutina));
            itemView.setOnClickListener(v -> listener.onRutinaClick(rutina));
        }
    }
}