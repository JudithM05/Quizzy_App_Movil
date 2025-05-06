package com.example.quizzyappmobil.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.ResultadoQuiz;
import com.example.quizzyappmobil.service.QuizAPI;

import java.util.List;
public class ResultadoQuizAdapter extends RecyclerView.Adapter<ResultadoQuizAdapter.ViewHolder> {

    private List<ResultadoQuiz> resultados;

    public ResultadoQuizAdapter(List<ResultadoQuiz> resultados) {
        this.resultados = resultados;
    }

    public void updateLista(List<ResultadoQuiz> nuevaLista) {
        this.resultados = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado_quiz, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultadoQuiz resultado = resultados.get(position);
        holder.tvNombre.setText(resultado.getUsuario().getNombre());
        holder.tvPuntaje.setText(resultado.getPuntaje() + " pts");

        Glide.with(holder.itemView.getContext())
                .load(QuizAPI.BASE_URL + "/uploads/users/" + resultado.getUsuario().getAvatar())
                .placeholder(R.drawable.avatar_predeterminado)
                .circleCrop()
                .into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPuntaje;
        ImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPuntaje = itemView.findViewById(R.id.tvPuntaje);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
