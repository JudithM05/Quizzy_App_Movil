package com.example.quizzyappmobil.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizzyappmobil.Pantallas.PantallaQuiz;
import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private Context context;
    private OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onPlayClick(Quiz quiz);
        void onFavoriteClick(Quiz quiz, int position);
        void onQuizClick(Quiz quiz);
    }

    public QuizAdapter(List<Quiz> quizList, Context context, OnQuizClickListener listener) {
        this.quizList = quizList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        holder.tituloQuiz.setText(quiz.getNombre());
        holder.autorQuiz.setText("Autor: " + quiz.getNombreAutor());

        // Cargar imagen del quiz usando Glide
        if (quiz.getImagen() != null && !quiz.getImagen().isEmpty()) {
            // Construir URL completa si es necesario
            String imageUrl = "http://192.168.38.2:8000" + quiz.getImagen();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_quiz)
                    .error(R.drawable.placeholder_quiz)
                    .centerCrop()
                    .into(holder.imagenQuiz);
        } else {
            // Cargar imagen predeterminada
            holder.imagenQuiz.setImageResource(R.drawable.placeholder_quiz);
        }

        // Actualizar icono de favorito según estado
        if (quiz.isEsFavorito()) {
            holder.botonFavorito.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.botonFavorito.setImageResource(R.drawable.ic_favorite_border);
        }

        // Configurar listeners
        holder.botonJugar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClick(quiz);
                // En tu actividad que implementa el listener, tendrías algo como:
                Intent intent = new Intent(context, PantallaQuiz.class);
                intent.putExtra("QUIZ_ID", quiz.getId());
                context.startActivity(intent);
            }
        });

        holder.botonFavorito.setOnClickListener(v -> {
            if (listener != null) {
                quiz.setEsFavorito(!quiz.isEsFavorito());
                holder.botonFavorito.setImageResource(
                        quiz.isEsFavorito() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
                );
                listener.onFavoriteClick(quiz, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList != null ? quizList.size() : 0;
    }

    public void updateData(List<Quiz> newQuizList) {
        this.quizList = newQuizList;
        notifyDataSetChanged();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenQuiz;
        TextView tituloQuiz, autorQuiz;
        Button botonJugar;
        ImageButton botonFavorito;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenQuiz = itemView.findViewById(R.id.imagen_quiz);
            tituloQuiz = itemView.findViewById(R.id.titulo_quiz);
            autorQuiz = itemView.findViewById(R.id.autor_quiz);
            botonJugar = itemView.findViewById(R.id.boton_jugar);
            botonFavorito = itemView.findViewById(R.id.boton_favorito);
        }
    }
}
