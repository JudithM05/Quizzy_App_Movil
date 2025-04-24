package com.example.quizzyappmobil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.Resultado;
import com.bumptech.glide.Glide;

import java.util.List;

public class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ViewHolder> {

    private List<Resultado> listaResultados;
    private Context context;

    public ResultadoAdapter(Context context, List<Resultado> resultados) {
        this.context = context;
        this.listaResultados = resultados;
    }

    @Override
    public ResultadoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resultado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultadoAdapter.ViewHolder holder, int position) {
        Resultado resultado = listaResultados.get(position);
        holder.textViewNombreUsuario.setText(resultado.getNombreUsuario());
        holder.textViewPuntos.setText(resultado.getPuntos() + " pts");

        // Cargar avatar con Glide
        if (resultado.getAvatar() != null && !resultado.getAvatar().isEmpty()) {
            String urlAvatar = "https://tuservidor.com/avatars/" + resultado.getAvatar();
            Glide.with(context)
                    .load(urlAvatar)
                    .placeholder(R.drawable.avatar_predeterminado)
                    .into(holder.imageViewAvatar);
        } else {
            holder.imageViewAvatar.setImageResource(R.drawable.avatar_predeterminado);
        }
    }

    @Override
    public int getItemCount() {
        return listaResultados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewNombreUsuario, textViewPuntos;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);
            textViewPuntos = itemView.findViewById(R.id.textViewPuntos);
        }
    }
}

