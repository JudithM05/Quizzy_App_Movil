package com.example.quizzyappmobil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.ResultadoQuiz;
import com.example.quizzyappmobil.service.QuizAPI;

import java.util.List;

public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ResultadoViewHolder> {

    private Context context;
    private List<ResultadoQuiz> resultados;

    public ResultadosAdapter(Context context, List<ResultadoQuiz> resultados) {
        this.context = context;
        this.resultados = resultados;
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resultado_quiz, parent, false);
        return new ResultadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        ResultadoQuiz resultado = resultados.get(position);

        // Configurar el número de posición (empezamos desde 1)
        holder.tvPosicion.setText((position + 1) + ".");

        // Configurar nombre y puntaje
        if (resultado.getUsuario() != null) {
            holder.tvNombreUsuario.setText(resultado.getUsuario().getNombre());

            // Cargar imagen de perfil con Glide
            if (resultado.getUsuario().getAvatar() != null && !resultado.getUsuario().getAvatar().isEmpty()) {
                String imageUrl = QuizAPI.BASE_URL + "/uploads/users/" + resultado.getUsuario().getAvatar();

                Glide.with(context)
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.avatar_predeterminado)
                        .error(R.drawable.avatar_predeterminado)
                        .into(holder.ivFotoPerfil);
            } else {
                holder.ivFotoPerfil.setImageResource(R.drawable.avatar_predeterminado);
            }
        } else {
            holder.tvNombreUsuario.setText("Usuario " + resultado.getUsuarioId());
            holder.ivFotoPerfil.setImageResource(R.drawable.avatar_predeterminado);
        }

        // Mostrar puntaje
        holder.tvPuntaje.setText(resultado.getPuntaje() + " pts");
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosicion, tvNombreUsuario, tvPuntaje;
        ImageView ivFotoPerfil;

        public ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosicion = itemView.findViewById(R.id.tvPosicion);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvPuntaje = itemView.findViewById(R.id.tvPuntaje);
            ivFotoPerfil = itemView.findViewById(R.id.ivFotoPerfil);
        }
    }
}