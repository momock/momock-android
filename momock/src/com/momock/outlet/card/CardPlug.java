package com.momock.outlet.card;

import com.momock.holder.FragmentHolder;
import com.momock.holder.IComponentHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Plug;

public abstract class CardPlug extends Plug implements ICardPlug{
	public static CardPlug get(final ViewHolder vh){
		return new CardPlug(){

			@Override
			public IComponentHolder getComponent() {
				return vh;
			}
		};
	}
	public static CardPlug get(final FragmentHolder fh){
		return new CardPlug(){

			@Override
			public IComponentHolder getComponent() {
				return fh;
			}
		};
	}
}
