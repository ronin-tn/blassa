/*
  Register Page
  -------------
  Registration form matching RegisterRequest DTO.
  
  Maps to: POST /auth/register
  DTO: RegisterRequest { 
    email, password, firstName, lastName, 
    phoneNumber (E.164), gender, birthDate 
  }
*/

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/common';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { Label } from '@/components/ui/label';
import { Gender } from '@/utils/constants';

const Register = () => {
    const navigate = useNavigate();
    const { register } = useAuth();

    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        gender: '',
        birthDate: '',
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');

    // Handle input changes
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
        setApiError('');
    };

    // Handle select changes
    const handleGenderChange = (value) => {
        setFormData(prev => ({ ...prev, gender: value }));
        if (errors.gender) {
            setErrors(prev => ({ ...prev, gender: '' }));
        }
    };

    // Validate form
    const validate = () => {
        const newErrors = {};

        if (!formData.email) {
            newErrors.email = 'Email requis';
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Email invalide';
        }

        if (!formData.password) {
            newErrors.password = 'Mot de passe requis';
        } else if (formData.password.length < 6) {
            newErrors.password = 'Minimum 6 caractères';
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Les mots de passe ne correspondent pas';
        }

        if (!formData.firstName) {
            newErrors.firstName = 'Prénom requis';
        }

        if (!formData.lastName) {
            newErrors.lastName = 'Nom requis';
        }

        if (!formData.phoneNumber) {
            newErrors.phoneNumber = 'Numéro de téléphone requis';
        } else if (!/^\+[1-9][0-9]{7,14}$/.test(formData.phoneNumber)) {
            newErrors.phoneNumber = 'Format: +21612345678';
        }

        if (!formData.gender) {
            newErrors.gender = 'Genre requis';
        }

        if (!formData.birthDate) {
            newErrors.birthDate = 'Date de naissance requise';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // Handle form submit
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

        setIsLoading(true);
        setApiError('');

        try {
            // Prepare data for API (exclude confirmPassword)
            const { confirmPassword, ...registerData } = formData;

            await register(registerData);
            navigate('/'); // Redirect to home on success
        } catch (error) {
            const message = error.response?.data?.message || 'Erreur lors de l\'inscription';
            setApiError(message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-background p-4 py-8">
            <Card className="w-full max-w-md">
                <CardHeader className="text-center">
                    <CardTitle className="text-2xl font-bold text-primary">
                        🚗 Blassa
                    </CardTitle>
                    <CardDescription>
                        Créer un compte
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* API Error */}
                        {apiError && (
                            <div className="p-3 rounded-md bg-destructive/10 text-destructive text-sm">
                                {apiError}
                            </div>
                        )}

                        {/* Name Row */}
                        <div className="grid grid-cols-2 gap-3">
                            <Input
                                label="Prénom"
                                name="firstName"
                                placeholder="Ahmed"
                                value={formData.firstName}
                                onChange={handleChange}
                                error={errors.firstName}
                            />
                            <Input
                                label="Nom"
                                name="lastName"
                                placeholder="Ben Ali"
                                value={formData.lastName}
                                onChange={handleChange}
                                error={errors.lastName}
                            />
                        </div>

                        <Input
                            label="Email"
                            name="email"
                            type="email"
                            placeholder="votre@email.com"
                            value={formData.email}
                            onChange={handleChange}
                            error={errors.email}
                            autoComplete="email"
                        />

                        <Input
                            label="Téléphone"
                            name="phoneNumber"
                            type="tel"
                            placeholder="+21612345678"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            error={errors.phoneNumber}
                        />

                        {/* Gender Select */}
                        <div className="space-y-2">
                            <Label className={errors.gender ? 'text-destructive' : ''}>
                                Genre
                            </Label>
                            <Select value={formData.gender} onValueChange={handleGenderChange}>
                                <SelectTrigger className={errors.gender ? 'border-destructive' : ''}>
                                    <SelectValue placeholder="Sélectionnez..." />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={Gender.MALE}>Homme</SelectItem>
                                    <SelectItem value={Gender.FEMALE}>Femme</SelectItem>
                                </SelectContent>
                            </Select>
                            {errors.gender && (
                                <p className="text-sm text-destructive">{errors.gender}</p>
                            )}
                        </div>

                        <Input
                            label="Date de naissance"
                            name="birthDate"
                            type="date"
                            value={formData.birthDate}
                            onChange={handleChange}
                            error={errors.birthDate}
                        />

                        <Input
                            label="Mot de passe"
                            name="password"
                            type="password"
                            placeholder="••••••••"
                            value={formData.password}
                            onChange={handleChange}
                            error={errors.password}
                            autoComplete="new-password"
                        />

                        <Input
                            label="Confirmer le mot de passe"
                            name="confirmPassword"
                            type="password"
                            placeholder="••••••••"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            error={errors.confirmPassword}
                            autoComplete="new-password"
                        />

                        <Button
                            type="submit"
                            variant="action"
                            size="lg"
                            className="w-full"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Inscription...' : 'S\'inscrire'}
                        </Button>
                    </form>

                    <div className="mt-6 text-center text-sm text-muted-foreground">
                        <p>
                            Déjà un compte ?{' '}
                            <Link to="/login" className="text-primary hover:underline font-medium">
                                Se connecter
                            </Link>
                        </p>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
};

export default Register;
