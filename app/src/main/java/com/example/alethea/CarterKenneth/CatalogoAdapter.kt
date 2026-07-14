package com.example.alethea.CarterKenneth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.MejiaRachel.Libro
import com.example.alethea.R

class CatalogoAdapter(
    private val listaLibros: List<Libro>,
    private val onClick: ((Libro) -> Unit)? = null,
    private val onFavorito: ((Libro) -> Unit)? = null
) : RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder>() {

    class CatalogoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloLibro)
        val tvAutor: TextView = view.findViewById(R.id.tvAutorLibro)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoriaLibro)
        val tvAnio: TextView = view.findViewById(R.id.tvAnioLibro)
        val tvStock: TextView = view.findViewById(R.id.tvStockCatalogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CatalogoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalogo_libro, parent, false)
        return CatalogoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogoViewHolder, position: Int) {
        val libro = listaLibros[position]
        holder.tvTitulo.text = libro.nombre
        holder.tvAutor.text = libro.autor
        holder.tvCategoria.text = libro.categoria
        holder.tvAnio.text = libro.anio
        holder.tvStock.text = holder.itemView.context.getString(
            R.string.ken_disponibles_formato,
            libro.disponibles,
            libro.stock
        )

        holder.itemView.setOnClickListener { onClick?.invoke(libro) }
        holder.itemView.findViewById<View>(R.id.btnFav).setOnClickListener { onFavorito?.invoke(libro) }
    }

    override fun getItemCount(): Int = listaLibros.size
}
