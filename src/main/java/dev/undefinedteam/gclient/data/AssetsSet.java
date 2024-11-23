package dev.undefinedteam.gclient.data;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class AssetsSet {
    @SerializedName("assets")
    public List<AssetData> assetData;

    public AssetsSet() {
        this.assetData = new ArrayList<>();
    }

    public AssetData find(String location) {
        return assetData.stream().filter(a -> a.location.equals(location)).findFirst().orElse(null);
    }

    public int size() {
        return assetData.size();
    }

    public boolean isEmpty() {
        return assetData.isEmpty();
    }

    public boolean contains(Object o) {
        return assetData.contains(o);
    }

    public Iterator<AssetData> iterator() {
        return assetData.iterator();
    }

    public Object[] toArray() {
        return assetData.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return assetData.toArray(a);
    }

    public boolean add(AssetData o) {
        return assetData.add(o);
    }

    public boolean remove(Object o) {
        return assetData.remove(o);
    }

    public boolean addAll(Collection<? extends AssetData> c) {
        return assetData.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends AssetData> c) {
        return assetData.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return assetData.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return assetData.retainAll(c);
    }

    public void clear() {
        assetData.clear();
    }

    public AssetData get(int index) {
        return assetData.get(index);
    }

    public AssetData set(int index, AssetData element) {
        return assetData.set(index, element);
    }

    public void add(int index, AssetData element) {
        assetData.add(index, element);
    }

    public AssetData remove(int index) {
        return assetData.remove(index);
    }

    public int indexOf(Object o) {
        return assetData.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return assetData.lastIndexOf(o);
    }

    public ListIterator<AssetData> listIterator() {
        return assetData.listIterator();
    }

    public ListIterator<AssetData> listIterator(int index) {
        return assetData.listIterator(index);
    }

    public List<AssetData> subList(int fromIndex, int toIndex) {
        return assetData.subList(fromIndex, toIndex);
    }
}
