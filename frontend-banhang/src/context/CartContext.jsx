
import { createContext, useContext, useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
    const { user } = useAuth();
    const [cartItems, setCartItems] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem("cart")) || [];
        } catch {
            return [];
        }
    });
    const [cartId, setCartId] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!user?.id) {
            localStorage.setItem("cart", JSON.stringify(cartItems));
        }
    }, [cartItems, user?.id]);

    useEffect(() => {
        if (!user?.id) return;

        const loadCart = async () => {
            try {
                setLoading(true);
                const { data } = await api.get(`/api/cart/${user.id}`);
                setCartId(data.id);
                setCartItems(
                    (data.items || []).map((item) => ({
                        id: item.productId,
                        cartItemId: item.id,
                        name: item.productName,
                        price: Number(item.price),
                        quantity: item.quantity,
                        image: "",
                    }))
                );
            } catch (error) {
                console.error("Không thể tải giỏ hàng:", error);
                if (error.response?.status === 401) {
                    setCartItems([]);
                    setCartId(null);
                }
            } finally {
                setLoading(false);
            }
        };

        loadCart();
    }, [user?.id]);

    const addToCart = async (product) => {
        if (user?.id) {
            const existing = cartItems.find((item) => item.id === product.id);
            const response = existing
                ? await api.put(`/api/cart/${user.id}/items/${existing.cartItemId}`, {
                    quantity: existing.quantity + 1,
                })
                : await api.post(`/api/cart/${user.id}/items`, {
                    productId: product.id,
                    quantity: 1,
                });
            const data = response.data;
            setCartId(data.id);
            setCartItems((data.items || []).map((item) => ({
                id: item.productId,
                cartItemId: item.id,
                name: item.productName,
                price: Number(item.price),
                quantity: item.quantity,
                image: product.image || "",
            })));
            return;
        }

        setCartItems((current) => {
            const existing = current.find((item) => item.id === product.id);

            if (existing) {
                return current.map((item) =>
                    item.id === product.id
                        ? { ...item, quantity: item.quantity + 1 }
                        : item
                );
            }

            return [...current, { ...product, quantity: 1 }];
        });
    };

    const updateQuantity = async (id, quantity) => {
        if (quantity <= 0) {
            await removeFromCart(id);
            return;
        }

        if (user?.id) {
            const item = cartItems.find((entry) => entry.id === id);
            const { data } = await api.put(`/api/cart/${user.id}/items/${item.cartItemId}`, { quantity });
            setCartItems((data.items || []).map((entry) => ({
                id: entry.productId,
                cartItemId: entry.id,
                name: entry.productName,
                price: Number(entry.price),
                quantity: entry.quantity,
                image: item.image,
            })));
            return;
        }

        setCartItems((current) =>
            current.map((item) =>
                item.id === id ? { ...item, quantity } : item
            )
        );
    };

    const removeFromCart = async (id) => {
        if (user?.id) {
            const item = cartItems.find((entry) => entry.id === id);
            await api.delete(`/api/cart/${user.id}/items/${item.cartItemId}`);
        }
        setCartItems((current) => current.filter((item) => item.id !== id));
    };

    const clearCart = async () => {
        if (user?.id) {
            await api.delete(`/api/cart/${user.id}`);
        }
        setCartItems([]);
        setCartId(null);
    };

    const totalItems = cartItems.reduce(
        (total, item) => total + item.quantity,
        0
    );

    const totalPrice = cartItems.reduce(
        (total, item) => total + item.price * item.quantity,
        0
    );

    return (
        <CartContext.Provider
            value={{
                cartItems,
                addToCart,
                updateQuantity,
                removeFromCart,
                clearCart,
                totalItems,
                totalPrice,
                cartId,
                loading,
            }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    return useContext(CartContext);
}
