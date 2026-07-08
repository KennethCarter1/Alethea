package com.example.alethea.MejiaRachel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R

data class Devolucion(
    val prestamoId: Int,
    val usuarioNombre: String,
    val libroNombre: String,
    val fechaPrestamo: String,
    val fechaDevolucion: String?,
    val estado: String
)

class DevolucionAdapter(
    private val devoluciones: List<Devolucion>
) : RecyclerView.Adapter<DevolucionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvLibro: TextView = view.findViewById(R.id.tvLibro)
        val tvFechaPrestamo: TextView = view.findViewById(R.id.tvFechaPrestamo)
        val tvFechaLimite: TextView = view.findViewById(R.id.tvFechaLimite)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_devolucion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val d = devoluciones[position]
        holder.tvUsuario.text = d.usuarioNombre
        holder.tvLibro.text = d.libroNombre
        holder.tvFechaPrestamo.text = d.fechaPrestamo
        holder.tvFechaLimite.text = d.fechaDevolucion ?: "-"
        holder.tvEstado.text = d.estado
        when (d.estado) {
            "Aceptada" -> {
                holder.tvEstado.setBackgroundResource(R.drawable.badge_aceptada)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
            "Rechazado" -> {
                holder.tvEstado.setBackgroundResource(R.drawable.badge_rechazado)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
            else -> {
                holder.tvEstado.setBackgroundResource(R.drawable.badge_sinstock)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
        }
    }

    override fun getItemCount(): Int = devoluciones.size
}