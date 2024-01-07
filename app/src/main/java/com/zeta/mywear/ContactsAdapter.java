package com.zeta.mywear;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Intervento> mInterventos;
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView impiantoTextView;
        public TextView tipoAttivitaTextView;
        public TextView dataRichiestaTextView;
        public LinearLayout myLinearLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            // Imposta un ClickListener sull'elemento della lista
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Gestisci il click sull'elemento, ad esempio, apri un'altra Activity
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Ottieni il dato associato all'elemento cliccato
                        Intervento selectedItem = mInterventos.get(position);
                        // Apri l'Activity di dettaglio con il dato associato
                        Intent intent = new Intent(itemView.getContext(), UpdateActivity.class);
                        intent.putExtra("id_intervento", selectedItem.getId_intervento());
                        intent.putExtra("id_stato", selectedItem.getId_stato());
                        intent.putExtra("impianto", selectedItem.getImpianto());
                        intent.putExtra("data_richiesta", selectedItem.getData_richiesta());
                        intent.putExtra("tipo_intervento", selectedItem.getTipo_intervento());
                        intent.putExtra("stato", selectedItem.getStato());
                        itemView.getContext().startActivity(intent);
                    }
                }
            });


            impiantoTextView = (TextView) itemView.findViewById(R.id.impianto);
            tipoAttivitaTextView = (TextView) itemView.findViewById(R.id.tipoAttivita);
            dataRichiestaTextView = (TextView) itemView.findViewById(R.id.data_richiesta);
            myLinearLayout = itemView.findViewById(R.id.contenitoreIntervento);
        }
    }

    public ContactsAdapter(List<Intervento> interventos) {
        mInterventos = interventos;
    }
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.my_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Intervento intervento = mInterventos.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.impiantoTextView;
        textView.setText(intervento.getImpianto());

        TextView textViewTipoAttivita = holder.tipoAttivitaTextView;
        textViewTipoAttivita.setText(intervento.getTipo_intervento());

        TextView textData = holder.dataRichiestaTextView;
        textData.setText(intervento.getData_richiesta());


        if(Objects.equals(TypeStato.APERTO.getId(), intervento.getId_stato()))    {
            holder.myLinearLayout.setBackgroundResource(R.drawable.rounded_button_aperti);
        }else if (Objects.equals(TypeStato.IN_CORSO.getId(), intervento.getId_stato())){
            holder.myLinearLayout.setBackgroundResource(R.drawable.rounded_button_incorso);
        }else if(Objects.equals(TypeStato.CHIUSO.getId(), intervento.getId_stato())){
            holder.myLinearLayout.setBackgroundResource(R.drawable.rounded_button_chiusi);
        }


        // Button button = holder.messageButton;
        // button.setText(contact.isOnline() ? "Message" : "Offline");
        // button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mInterventos.size();
    }

}
